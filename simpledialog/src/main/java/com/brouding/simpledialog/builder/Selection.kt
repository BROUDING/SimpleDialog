package com.brouding.simpledialog.builder

import android.content.Context
import com.brouding.simpledialog.BtnActionCallback
import com.brouding.simpledialog.SelectionCallback
import com.brouding.simpledialog.extensions.inDp
import com.brouding.simpledialog.extra.Type

class Selection(override val context: Context): General(context) {
    var selectionCallback: SelectionCallback? = null
    var selectionList: List<String>? = null
    var paddingLeftDp: Int? = 14

    init {
        type = Type.SELECTION
    }

    fun applyGeneral(func: General.() -> Unit): Selection {
        this.func()
        return this
    }

    fun setContentList(contentList: List<String>, paddingLeftDp: Int? = 14): Selection {
        selectionList = contentList
        paddingLeftDp?.let { this.paddingLeftDp = context.inDp(paddingLeftDp) }
        return this
    }

    fun onSelect(callback: SelectionCallback? = null): Selection {
        selectionCallback = callback
        return this
    }

    override fun checkBeforeShow() {
        checkNotNull(selectionList) { "SelectionBuilder - selectionList needs to be set" }
        checkNotNull(selectionCallback) { "SelectionBuilder - selectionCallback needs to be set" }
    }
}