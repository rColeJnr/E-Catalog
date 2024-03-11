package com.rick.auth_screen

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rick.auth_screen.databinding.FragmentAuthenticationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthenticationFragment : Fragment() {

    private var _binding: FragmentAuthenticationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthenticationViewModel by viewModels()

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

    private lateinit var contract: ActivityResultLauncher<Intent>
    private lateinit var intentContract: ActivityResultLauncher<IntentSenderRequest>
    private var userDeclinedOneTap = false
    private lateinit var db: FirebaseFirestore


    override fun onStart() {
        super.onStart()
        // check user's internet connection
        if (checkIfOnline()) {
            showOneTapUI(auth.currentUser)
        } else {
            Toast.makeText(
                requireContext(), "Connection Error!\nCheck your internet connection.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(requireContext())
        db = Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAuthenticationBinding.inflate(inflater, container, false)

        val isOnline by mutableStateOf(checkIfOnline())

        binding.root.setContent {
            Login(
                onAuthenticate = { email, password -> signInWithPassword(email, password) },
                onCreateAccount = { email, password, username ->
                    signUpWithPassword(
                        email,
                        password,
                        username
                    )
                },
                onGoogleOneTap = { signIn() },
                onPasswordReset = { email -> onPasswordReset(email) },
                viewModel
            )
        }

        setupAuth()

        contract =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    data?.let { onResultReceived(it) }
                }
            }

        intentContract =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                if (result != null) {
                    val data = result.data
                    data?.let { onSenderReceived(it) }
                }
            }

        return binding.root
    }


    private fun onPasswordReset(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener {
                Toast.makeText(requireContext(), "Password reset link sent", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun navigate() {
        val request = NavDeepLinkRequest.Builder
            .fromUri("myapp://com.rick.auth-screen/BestsellerFragment".toUri())
            .build()
        findNavController().popBackStack()
        findNavController().navigate(request)
    }

    private fun saveUserToDb(username: String) {
        val user = hashMapOf("username" to username)
        db.collection("user")
            .add(user)
    }

    //    private fun readUserFromDb() {
//        db.collection("user")
//            .get()
//            .addOnSuccessListener { result ->
//                for (document in result) {
//                    Log.d(TAG, "${document.id} => ${document.data}")
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.w(TAG, "Error getting documents.", exception)
//            }
//    }
    private fun setupAuth() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("185760784140-6o04up90to11vk15fl0kko5hr1fdmcbr.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        auth = Firebase.auth
        oneTapClient = Identity.getSignInClient(requireContext())
        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(
                BeginSignInRequest.PasswordRequestOptions.builder().setSupported(true).build()
            )
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId("185760784140-6o04up90to11vk15fl0kko5hr1fdmcbr.apps.googleusercontent.com")
                    .build()
            ).build()
    }

    // One tap sign in/ auto
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        contract.launch(signInIntent)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // user authenticated
                    navigate()
                } else {
                    // failed to authenticate
                    Snackbar.make(
                        requireView(),
                        "Authentication with credential Failed.",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun isValidPassword(password: String): Boolean {
        if (password.isNotBlank()) return password.matches(PASSWORD_REGEX)
        return false
    }


    private fun isValidUsername(username: String): Boolean {
        if (username.isNotBlank()) return username.matches(USERNAME_REGEX)
        return false
    }

    private fun signUpWithPassword(email: String, password: String, username: String) {
        if (
            isValidEmail(email) &&
            isValidPassword(password) &&
            isValidUsername(username)
        ) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { authResult ->
                    val currentUser = authResult.user
                    saveUserToDb(username)
                    navigate()
                }
                .addOnFailureListener {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        requireContext(), "Account creation failed. ${it.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        } else {
            Toast.makeText(
                requireContext(),
                "Failed to create account, Please confirm that you meet all of the requirements",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun signInWithPassword(email: String, password: String) {
        if (email.isNotBlank() && password.isNotBlank()) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { authResult ->
                    val currentUser = authResult.user
                    navigate()
                }
                .addOnFailureListener {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        requireContext(),
                        "Invalid login credentials.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } else {
            Toast.makeText(
                requireContext(), "Please fill in your email and password, then try again.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    @Suppress("DEPRECATION")
    private fun checkIfOnline(): Boolean {
        val cm = ContextCompat.getSystemService(requireContext(), ConnectivityManager::class.java)

        return if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O) {
            val capabilities = cm?.getNetworkCapabilities(cm.activeNetwork) ?: return false
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) && capabilities.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_VALIDATED
            )
        } else {
            cm?.activeNetworkInfo?.isConnectedOrConnecting == true
        }
    }

    // Show google one tap authentication on app start
    private fun showOneTapUI(user: FirebaseUser?) {
        if (user != null) {
            //move forward
            navigate()
        } else {
            oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener { result ->
                    try {
                        val intent =
                            IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                        intentContract.launch(intent)
                    } catch (_: IntentSender.SendIntentException) {
                        // do nothing and continue presenting the signed-out UI.
                    }
                }
        }
    }

    // Result for start activity for result
    private fun onResultReceived(data: Intent) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            // Google sign in was successful, authenticate with firebase
            val account = task.getResult(ApiException::class.java)
            account?.idToken?.let { firebaseAuthWithGoogle(it) }
        } catch (e: ApiException) {
            // Google sign in failed
            Toast.makeText(
                requireContext(), "Authentication failed with error:\n${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // Result for start intent sender for result
    private fun onSenderReceived(data: Intent) {
        try {
            val credential = oneTapClient.getSignInCredentialFromIntent(data)
            credential.googleIdToken?.let { token ->
                firebaseAuthWithGoogle(token)
                return
            }
            credential.password?.let { password ->
                signInWithPassword(credential.id, password)
            }
        } catch (e: ApiException) {
            when (e.statusCode) {
                CommonStatusCodes.CANCELED -> userDeclinedOneTap = true
                CommonStatusCodes.NETWORK_ERROR -> {} // no internet
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        fun isValidEmail(email: String): Boolean {
            if (email.isNotBlank()) return Patterns.EMAIL_ADDRESS.matcher(email).matches()
            return false
        }

    }
}

// No white spaces, At least # characters.
private val PASSWORD_REGEX = "^(?=\\S+$).{6,}$".toRegex()
private val USERNAME_REGEX = "^(?=\\S+$).{3,12}$".toRegex()