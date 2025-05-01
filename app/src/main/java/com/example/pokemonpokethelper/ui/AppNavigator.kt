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
                userId = currentUserId,
                onCreateCollection = { nav.navigate("addCollection") },
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
            "addCard/{collectionId}",
            arguments = listOf(navArgument("collectionId") { type = NavType.StringType })
        ) { backStack ->
            val cid = backStack.arguments!!.getString("collectionId")!!
            AddCardScreen(collectionId = cid) {
                nav.popBackStack()
            }
        }
    }
}