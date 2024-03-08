import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.tplogin.R
import com.example.tplogin.databinding.ProfilActivityBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import java.io.File

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var nameEditText: TextInputEditText
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var viewImage: ImageView  // Ajout de cette ligne pour éviter une erreur
    private lateinit var filePhoto: File  // Ajout de cette ligne pour éviter une erreur
    private val PICK_IMAGE_REQUEST = 1

    companion object {
        private const val IMAGE_CHOOSE = 1000
        private const val PERMISSION_CODE = 1001
        private const val FILE_NAME = "temp_photo"
        private const val REQUEST_CODE = 13
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: ProfilActivityBinding =
            DataBindingUtil.inflate(inflater, R.layout.profil_activity, container, false)

        auth = FirebaseAuth.getInstance()

        // Initialisation des champs de texte
        nameEditText = binding.name
        emailEditText = binding.email
        passwordEditText = binding.password
        viewImage = binding.profileImage  // Ajout de cette ligne pour éviter une erreur

        // Chargement des informations actuelles de l'utilisateur
        loadUserData()

        // Gestionnaire de clic sur le bouton de mise à jour
        val updateButton: Button = binding.btnEditProfile
        updateButton.setOnClickListener {
            updateUserData()
        }

        // Gestionnaire de clic sur le bouton d'édition de profil
        val editProfileButton: Button = binding.btnEditProfile
        editProfileButton.setOnClickListener {
            // Activer l'édition des champs de texte
            enableEditTextFields()
        }

        // Trouvez le bouton dans la vue du fragment
        val choosePhotoButton: Button = binding.btnChoosePhoto
        val takePhotoButton: Button = binding.btnTakePhoto

        choosePhotoButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_DENIED
                ) {
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permissions, PERMISSION_CODE)
                } else {
                    chooseImageGallery()
                }
            } else {
                chooseImageGallery()
            }
        }

        takePhotoButton.setOnClickListener {
            val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            filePhoto = getPhotoFile(FILE_NAME)
            val providerFile = FileProvider.getUriForFile(
                requireContext(),
                "com.example.androidcamera.fileprovider",
                filePhoto
            )
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerFile)
            startActivityForResult(takePhotoIntent, REQUEST_CODE)
        }

        return binding.root
    }


    private fun chooseImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_CHOOSE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    chooseImageGallery()
                } else {
                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == IMAGE_CHOOSE && resultCode == Activity.RESULT_OK) {
            viewImage.setImageURI(data?.data)
        } else if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val takenPhoto = BitmapFactory.decodeFile(filePhoto.absolutePath)
            viewImage.setImageBitmap(takenPhoto)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun getPhotoFile(fileName: String): File {
        val directoryStorage = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", directoryStorage)
    }

    private fun loadUserData() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val currentUserData = UserData(name = "John Doe", email = currentUser.email ?: "")
            nameEditText.setText(currentUserData.name)
            emailEditText.setText(currentUserData.email)
        }
    }

    private fun updateUserData() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val newName = nameEditText.text.toString()
            val newEmail = emailEditText.text.toString()
            val newPassword = passwordEditText.text.toString()

            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build()

            currentUser.updateProfile(profileUpdates)
                .addOnCompleteListener { profileTask ->
                    if (profileTask.isSuccessful) {
                        Log.d("ProfileFragment", "User profile updated successfully.")
                    } else {
                        Log.e("ProfileFragment", "Failed to update user profile.", profileTask.exception)
                    }

                    if (newEmail.isNotEmpty() && newEmail != currentUser.email) {
                        currentUser.updateEmail(newEmail)
                            .addOnCompleteListener { emailTask ->
                                if (emailTask.isSuccessful) {
                                    Log.d("ProfileFragment", "User email address updated successfully.")
                                } else {
                                    Log.e("ProfileFragment", "Failed to update user email address.", emailTask.exception)
                                }
                            }
                    }

                    if (newPassword.isNotEmpty()) {
                        currentUser.updatePassword(newPassword)
                            .addOnCompleteListener { passwordTask ->
                                if (passwordTask.isSuccessful) {
                                    Log.d("ProfileFragment", "User password updated successfully.")
                                } else {
                                    Log.e("ProfileFragment", "Failed to update user password.", passwordTask.exception)
                                }
                            }
                    }

                    Toast.makeText(requireContext(), "Modification terminée", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun enableEditTextFields() {
        nameEditText.isEnabled = true
        emailEditText.isEnabled = true
        passwordEditText.isEnabled = true
    }

    data class UserData(val name: String, val email: String)
}
