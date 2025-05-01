import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun AppNavigator() {
    val user = Firebase.auth.currentUser
    val nav = rememberNavController()
    val currentUserId = user?.uid ?: return

    NavHost(navController = nav, startDestination = "collections") {
        composable("collections") {
            CollectionsScreen(
                navController      = nav,
                userId             = currentUserId,
                onOpenCollection   = { name -> nav.navigate("cards/$name") }
            )
        }

        composable("addCollection") {
            AddCollectionScreen(onDone = { nav.popBackStack() })
        }
        composable(
                "cards/{collectionName}",
                arguments = listOf(navArgument("collectionName") { type = NavType.StringType })
        ) { backStack ->
                val collectionName = backStack.arguments!!.getString("collectionName")!!
                CardsScreen(
                      userId         = currentUserId,
                      collectionName = collectionName,
                      onAddCard      = { nav.navigate("addCard/$collectionName") },
                      onBack         = { nav.popBackStack() }
                            )
              }

        composable(
            "addCard/{collectionName}",                                          // ① path segment name
            arguments = listOf(navArgument("collectionName") { type = NavType.StringType })
        ) { backStack ->
            val collectionName = backStack.arguments!!.getString("collectionName")!!
            AddCardScreen(
                userId         = currentUserId,                                    // ② pass user
                collectionName = collectionName,                                   // ③ pass name, not “cid”
                onDone         = { nav.popBackStack() }
            )
        }
    }
}