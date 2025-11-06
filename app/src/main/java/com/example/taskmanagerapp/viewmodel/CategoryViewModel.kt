package com.example.taskmanagerapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanagerapp.data.local.entity.CategoryEntity
import com.example.taskmanagerapp.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _categories = MutableStateFlow<List<CategoryEntity>>(emptyList())
    val categories: StateFlow<List<CategoryEntity>> = _categories

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadCategories()
    }

    // 🔹 Cargar todas las categorías desde Room + sincronizar con Firebase
    fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            categoryRepository.getAllCategories().collect { localCategories ->
                _categories.value = localCategories
            }
            _isLoading.value = false

            // sincroniza categorías con la nube
            categoryRepository.syncCategories()
        }
    }

    // 🔹 Agregar nueva categoría
    fun addCategory(category: CategoryEntity) {
        viewModelScope.launch {
            categoryRepository.insertCategory(category)
            categoryRepository.syncCategories()
        }
    }

    // 🔹 Eliminar categoría
    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch {
            categoryRepository.deleteCategory(category)
            categoryRepository.syncCategories()
        }
    }
}
