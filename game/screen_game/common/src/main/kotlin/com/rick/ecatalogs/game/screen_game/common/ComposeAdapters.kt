package com.rick.ecatalogs.game.screen_game.common

import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rick.ecatalogs.data.ui_components.common.EcsTheme
import com.rick.ecatalogs.game.data_game.model.Game
import com.rick.ecatalogs.game.data_game.model.UserGame

class ComposeViewHolder(val view: ComposeView) : RecyclerView.ViewHolder(view)

private fun ViewGroup.composeView() = ComposeView(context).apply {
    layoutParams = RecyclerView.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT,
    )
    // Rows are recycled: keep the composition until the RecyclerView's lifecycle ends
    setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
}

/**
 * A Compose header as the first item of a RecyclerView (ConcatAdapter in front of the paged
 * list). paging-compose isn't used, so paged lists stay RecyclerViews with Compose rows.
 */
class ComposeHeaderAdapter(
    private val content: @Composable () -> Unit,
) : RecyclerView.Adapter<ComposeViewHolder>() {
    override fun getItemCount() = 1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ComposeViewHolder(parent.composeView())
    override fun onBindViewHolder(holder: ComposeViewHolder, position: Int) {
        holder.view.setContent { EcsTheme { content() } }
    }
}

/** Paged games, each row a [GameListItem] in a ComposeView. */
class GamePagingAdapter(
    private val onClick: (Game) -> Unit,
    private val onFavoriteClick: (Game, Boolean) -> Unit,
) : PagingDataAdapter<UserGame, ComposeViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ComposeViewHolder(parent.composeView())

    override fun onBindViewHolder(holder: ComposeViewHolder, position: Int) {
        val item = getItem(position) ?: return
        holder.view.setContent {
            EcsTheme { GameListItem(item, onClick, onFavoriteClick) }
        }
    }

    private companion object {
        val DIFF = object : DiffUtil.ItemCallback<UserGame>() {
            override fun areItemsTheSame(oldItem: UserGame, newItem: UserGame) = oldItem.game.id == newItem.game.id
            override fun areContentsTheSame(oldItem: UserGame, newItem: UserGame) = oldItem == newItem
        }
    }
}
