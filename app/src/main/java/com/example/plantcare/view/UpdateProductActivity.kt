package com.example.plantcare.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.plantcare.model.ProductModel
import com.example.plantcare.repository.ProductRepositoryImpl
import com.example.plantcare.viewmodel.ProductViewModel

class UpdateProductActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UpdateProductScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProductScreen() {
    val context = LocalContext.current
    val activity = context as? Activity

    // Get plant data from intent
    val plantId = activity?.intent?.getStringExtra("plant_id") ?: ""
    val initialPlantName = activity?.intent?.getStringExtra("plant_name") ?: ""
    val initialPlantType = activity?.intent?.getStringExtra("plant_type") ?: ""
    val initialPrice = activity?.intent?.getStringExtra("plant_price") ?: ""
    val initialDescription = activity?.intent?.getStringExtra("plant_description") ?: ""
    val initialImageUrl = activity?.intent?.getStringExtra("plant_image_url") ?: ""

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var plantName by remember { mutableStateOf(initialPlantName) }
    var plantType by remember { mutableStateOf(initialPlantType) }
    var price by remember { mutableStateOf(initialPrice) }
    var description by remember { mutableStateOf(initialDescription) }
    var currentImageUrl by remember { mutableStateOf(initialImageUrl) }

    val repo = remember { ProductRepositoryImpl() }
    val viewModel = remember { ProductViewModel(repo) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Update Plant") },
                navigationIcon = {
                    IconButton(onClick = {
                        activity?.finish()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4CAF50),
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Image Selection
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clickable {
                            imagePickerLauncher.launch("image/*")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        selectedImageUri != null -> {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Plant Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        currentImageUrl.isNotEmpty() -> {
                            AsyncImage(
                                model = currentImageUrl,
                                contentDescription = "Plant Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        else -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Photo",
                                    modifier = Modifier.size(48.dp),
                                    tint = Color(0xFF4CAF50)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Tap to change plant photo",
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }

            // Plant Name Field
            OutlinedTextField(
                value = plantName,
                onValueChange = { plantName = it },
                label = { Text("Plant Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4CAF50),
                    focusedLabelColor = Color(0xFF4CAF50)
                )
            )

            // Plant Type Field
            OutlinedTextField(
                value = plantType,
                onValueChange = { plantType = it },
                label = { Text("Plant Type/Category") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4CAF50),
                    focusedLabelColor = Color(0xFF4CAF50)
                )
            )

            // Price Field
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Price") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4CAF50),
                    focusedLabelColor = Color(0xFF4CAF50)
                )
            )

            // Description Field
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Care Instructions & Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4CAF50),
                    focusedLabelColor = Color(0xFF4CAF50)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Update Button
            Button(
                onClick = {
                    when {
                        plantName.isBlank() -> {
                            Toast.makeText(context, "Please enter plant name", Toast.LENGTH_SHORT).show()
                        }
                        plantType.isBlank() -> {
                            Toast.makeText(context, "Please enter plant type", Toast.LENGTH_SHORT).show()
                        }
                        price.isBlank() -> {
                            Toast.makeText(context, "Please enter price", Toast.LENGTH_SHORT).show()
                        }
                        description.isBlank() -> {
                            Toast.makeText(context, "Please enter care instructions", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            val priceValue = price.toDoubleOrNull()
                            if (priceValue == null) {
                                Toast.makeText(context, "Please enter valid price", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            Toast.makeText(context, "Updating plant...", Toast.LENGTH_SHORT).show()

                            // Check if image was changed
                            if (selectedImageUri != null) {
                                // Upload new image first
                                viewModel.uploadImage(context, selectedImageUri!!) { imageUrl ->
                                    if (imageUrl != null) {
                                        updatePlant(viewModel, plantId, plantName, priceValue, description, imageUrl, context, activity, plantType)
                                    } else {
                                        Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                // Use existing image
                                updatePlant(viewModel, plantId, plantName, priceValue, description, currentImageUrl, context, activity, plantType)
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                )
            ) {
                Text(
                    text = "Update Plant",
                    color = Color.White
                )
            }

            // Cancel Button
            OutlinedButton(
                onClick = {
                    activity?.finish()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF4CAF50)
                )
            ) {
                Text("Cancel")
            }
        }
    }
}

private fun updatePlant(
    viewModel: ProductViewModel,
    plantId: String,
    plantName: String,
    priceValue: Double,
    description: String,
    imageUrl: String,
    context: android.content.Context,
    activity: Activity?,
    plantType: String
) {
    // Convert data to MutableMap as expected by your updateProduct method
    val productData = mutableMapOf<String, Any?>(
        "productName" to plantName,
        "productPrice" to priceValue,
        "productDescription" to description,
        "productImageUrl" to imageUrl
    )

    // Call updateProduct with the correct parameters
    viewModel.updateProduct(plantId, productData) { success, message ->
        if (success) {
            val resultIntent = Intent().apply {
                putExtra("plant_id", plantId)
                putExtra("plant_name", plantName)
                putExtra("plant_type", plantType)
                putExtra("plant_description", description)
                putExtra("plant_price", priceValue.toString())
                putExtra("plant_image_url", imageUrl)
                putExtra("is_edit", true)
            }

            activity?.setResult(Activity.RESULT_OK, resultIntent)
            Toast.makeText(context, "Plant updated successfully!", Toast.LENGTH_SHORT).show()
            activity?.finish()
        } else {
            Toast.makeText(context, "Failed to update plant: $message", Toast.LENGTH_LONG).show()
        }
    }
}
