package org.bin.demo.uneodinary.view.event

import android.graphics.Bitmap
import android.net.Uri
import org.bin.demo.repository.model.TotalItems

sealed class NavigationEvent {
    data object NavigateToCameraFragment : NavigationEvent()

    data class NavigateTagSelect(val bitmap: Bitmap) : NavigationEvent()
    data class NavigateTagExpenseDetail(val totalItems: TotalItems) : NavigationEvent()
    data class ShareContent(val text: String) : NavigationEvent()
}