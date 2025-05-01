package com.example.foodieapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.foodieapp.R
import com.example.foodieapp.activities.SignIn
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

private lateinit var mAuth: FirebaseAuth
private lateinit var mGoogleSignInClient: GoogleSignInClient
private lateinit var btnSignOut:Button
private lateinit var profname:TextView
class ProfileFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnSignOut = view.findViewById(R.id.btnSignOut)
        profname = view.findViewById(R.id.profname)
        val emailTextView: TextView = view.findViewById(R.id.profileEmailTextView)
        val profileImage: ImageView = view.findViewById(R.id.profImage)

        mAuth = Firebase.auth
        val user = mAuth.currentUser

        profname.text = user?.displayName
        emailTextView.text = user?.email

        // Load profile image using Glide (recommended)
        val photoUrl = user?.photoUrl
        if (photoUrl != null) {
            Glide.with(this)
                .load(photoUrl)
                .centerCrop()
                .placeholder(R.drawable.userphoto1) // default if loading fails
                .into(profileImage)
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        btnSignOut.setOnClickListener {
            signOutAndStartSignInActivity()
        }
    }

    private fun signOutAndStartSignInActivity() {
        mAuth.signOut()
        mGoogleSignInClient.signOut().addOnCompleteListener(requireActivity()) {
            val intent = Intent(requireActivity(), SignIn::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }
}