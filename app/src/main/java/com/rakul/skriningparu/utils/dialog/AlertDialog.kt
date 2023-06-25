package com.rakul.skriningparu.utils.dialog

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface


fun showDialog(
    context: Context,
    title: String,
    message: String,
    positiveButtonText: String = "",
    negativeButtonText: String,
    isShowPositiveButton: Boolean = false,
    onEventPositiveClicked: (() -> Unit)? = null
) {
    val builder: AlertDialog.Builder = AlertDialog.Builder(context)

    // Set the message show for the Alert time

    // Set the message show for the Alert time
    builder.setMessage(message)

    // Set Alert Title

    // Set Alert Title
    builder.setTitle(title)

    // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show

    // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
    builder.setCancelable(false)

    // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.

    if (isShowPositiveButton) {
        // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
        builder.setPositiveButton(positiveButtonText,
            DialogInterface.OnClickListener { _: DialogInterface?, _: Int ->
                // When the user click yes button then app will close
                onEventPositiveClicked?.invoke()
            } as DialogInterface.OnClickListener)


    }
    // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.

    // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
    builder.setNegativeButton(negativeButtonText,
        DialogInterface.OnClickListener { dialog: DialogInterface, _: Int ->
            // If user click no then dialog box is canceled.
            dialog.cancel()
        } as DialogInterface.OnClickListener)

    // Create the Alert dialog

    // Create the Alert dialog
    val alertDialog: AlertDialog = builder.create()
    // Show the Alert Dialog box
    // Show the Alert Dialog box
    alertDialog.show()
}