import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun AppNavigator() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = "collections") {
        composable("collections") {
            CollectionsScreen(
                onCreateCollection = { nav.navigate("addCollection") },
                onOpenCollection   = { id -> nav.navigate("cards/$id") }
            )
        }

        composable("addCollection") {
            AddCollectionScreen(onDone = { nav.popBackStack() })
        }

        composable(
            "cards/{collectionId}",
            arguments = listOf(navArgument("collectionId") { type = NavType.StringType })
        ) { backStack ->
            val cid = backStack.arguments!!.getString("collectionId")!!
            CardsScreen(
                collectionId = cid,
                onAddCard    = { nav.navigate("addCard/$cid") },
                onBack       = { nav.popBackStack() }
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