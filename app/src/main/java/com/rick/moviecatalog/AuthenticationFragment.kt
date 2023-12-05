// package and imports

@AndroidEntryPoint
class AuthenticationFragment: Fragment() {

  private var _binding: FragmentAuthenticationBinding? = null
  private val binding get() = _binding!!
  private val viewModel: AuthenticationViewModel by viewModels()

  overwrite fun onCreateView(inflator: LayoutInflator, parent: ViewGroup, /*i actually forgot :(*/): View {
    _binding = FragmentAuthenticationBinding.inflate(parent, inflator, false) // is it inflator or inflater?

    binding.composeView.setContent{
      MdcTheme.something {
        AuthenticationScreen()0
      }
    }

    return binding.root
  }
  
}

@Composable
fun AuthencticationScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
                .background(color = Color(colorSurface)) //i would just like to state that i don't remember the last time i wrote compose code.
        ) {
        Text(
            text = "Discover what \nto binge watch \nnext, \nor read!", style = MaterialTheme.Typography.Body1,
            modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(x = 61.dp,
                                    y = 74.dp)
                        .border(border = BorderStroke(1.dp, Color(0xff27332d))))
    }
}
