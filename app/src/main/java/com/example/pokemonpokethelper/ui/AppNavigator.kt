package com.example.pokemonpokethelper.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun AppNavigator(currentUserId: String) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "collections") {
        composable("collections") {
            CollectionsScreen(
                navController    = navController,
                userId           = currentUserId,
                onOpenCollection = { name ->
                    navController.navigate("cards/$name")
                }
            )
        }

        composable("addCollection") {
            AddCollectionScreen(
                userId = currentUserId,
                onDone = { navController.popBackStack() }
            )
        }

        composable(
            route = "cards/{collectionName}",
            arguments = listOf(navArgument("collectionName") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val collectionName = backStackEntry.arguments!!
                .getString("collectionName")!!
            CardsScreen(
                userId         = currentUserId,
                collectionName = collectionName,
                onAddCard      = {
                    navController.navigate("addCard/$collectionName")
                },
                onBack         = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "addCard/{collectionName}",
            arguments = listOf(navArgument("collectionName") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val collectionName = backStackEntry.arguments!!
                .getString("collectionName")!!
            AddCardScreen(
                userId         = currentUserId,
                collectionName = collectionName,
                onDone         = { navController.popBackStack() }
            )
        }
    }
}
