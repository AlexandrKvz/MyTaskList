package com.xotkins.mytasklist.accounthelper

import android.widget.Toast
import com.google.firebase.auth.FirebaseUser
import com.xotkins.mytasklist.R
import com.xotkins.mytasklist.activities.MainActivity




class AccountHelper(act: MainActivity) {
    private val act = act

    fun signUpWithEmail(email: String, password: String){
        if(email.isNotEmpty() && password.isNotEmpty()){
            act.mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    senEmailVerification(task.result?.user!!)
                    act.uiUpdate(task.result?.user)
                }else{
                    Toast.makeText(act, act.resources.getString(R.string.sign_up_error), Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    fun signInWithEmail(email: String, password: String){
        if(email.isNotEmpty() && password.isNotEmpty()){
            act.mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if(task.isSuccessful){

                    act.uiUpdate(task.result?.user)
                }else{
                    Toast.makeText(act, act.resources.getString(R.string.sign_in_error), Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    private fun senEmailVerification(user: FirebaseUser){
        user.sendEmailVerification().addOnCompleteListener { task ->
            if(task.isSuccessful){
                Toast.makeText(act, act.resources.getString(R.string.send_email_verification_done), Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(act, act.resources.getString(R.string.send_email_verification_error), Toast.LENGTH_LONG).show()
            }
        }
    }
}