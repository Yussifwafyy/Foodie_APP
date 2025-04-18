package com.example.foodieapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.foodieapp.R

class ChatBotFragment : Fragment() {

    private lateinit var chatHistory: TextView
    private lateinit var userInput: EditText
    private lateinit var sendButton: Button

    private val foodItems = listOf(
        FoodItem("عدس", 20),
        FoodItem("رز", 15),
        FoodItem("بيض", 30),
        FoodItem("بطاطس", 20),
        FoodItem("مكرونة", 10),
        FoodItem("بصل", 5),
        FoodItem("زيت", 10),
        FoodItem("ملح", 2),
        FoodItem("طماطم", 10),
        FoodItem("فول", 12),
        FoodItem("عيش", 5),
        FoodItem("فلفل", 6),
        FoodItem("جبنة", 25),
        FoodItem("شعرية", 8),
        FoodItem("صلصة", 7),
        FoodItem("سمن", 15),
        FoodItem("لحمة", 120),
        FoodItem("فراخ", 90),
        FoodItem("سمك", 100),
        FoodItem("كبدة", 70),
        FoodItem("سجق", 65),
        FoodItem("جبنة رومي", 35),
        FoodItem("بيض بلدي", 40),
        FoodItem("مشروم", 45)
    )

    private val recipes = listOf(
        Recipe("كشري", listOf("عدس", "رز", "مكرونة", "بصل", "زيت", "ملح")),
        Recipe("عدس بشعرية", listOf("عدس", "شعرية", "زيت", "بصل", "ملح")),
        Recipe("بطاطس بالبيض", listOf("بطاطس", "بيض", "زيت", "ملح")),
        Recipe("مكرونة بالصلصة", listOf("مكرونة", "صلصة", "زيت", "ملح")),
        Recipe("بيض مسلوق مع بطاطس", listOf("بيض", "بطاطس", "ملح")),
        Recipe("فول بالطماطم", listOf("فول", "طماطم", "زيت", "ملح")),
        Recipe("عيش بالجبنة", listOf("عيش", "جبنة")),
        Recipe("شعرية بالسمن", listOf("شعرية", "سمن", "ملح")),
        Recipe("رز بشعرية", listOf("رز", "شعرية", "زيت", "ملح")),
        Recipe("بيض بالطماطم", listOf("بيض", "طماطم", "زيت", "ملح")),
        Recipe("جبنة بالطماطم", listOf("جبنة", "طماطم", "فلفل")),
        Recipe("عيش بالبيض", listOf("عيش", "بيض", "زيت")),
        Recipe("مكرونة بالسجق", listOf("مكرونة", "سجق", "بصل", "صلصة", "زيت")),
        Recipe("كبدة اسكندراني", listOf("كبدة", "بصل", "فلفل", "زيت", "عيش")),
        Recipe("فراخ مشوية", listOf("فراخ", "زيت", "ملح", "فلفل")),
        Recipe("لحمة بالخضار", listOf("لحمة", "بطاطس", "بصل", "طماطم")),
        Recipe("صينية سمك", listOf("سمك", "طماطم", "بصل", "زيت")),
        Recipe("بيتزا بالجبنة الرومي", listOf("مكرونة", "جبنة رومي", "طماطم", "زيت")),
        Recipe("بيض بالمشروم", listOf("بيض", "مشروم", "بصل", "زيت"))
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chatbot, container, false)

        chatHistory = view.findViewById(R.id.chatHistory)
        userInput = view.findViewById(R.id.userInput)
        sendButton = view.findViewById(R.id.sendButton)

        sendButton.setOnClickListener {
            val input = userInput.text.toString().trim()
            appendChat("أنت: $input")

            val budget = extractBudget(input)

            if (budget != null) {
                val botReply = generateSuggestions(budget)
                appendChat("بوت الأكل: $botReply")
            } else {
                appendChat("بوت الأكل: من فضلك اكتب الميزانية (مثلاً: 100 أو 'معايا 150')")
            }

            userInput.text.clear()
        }

        return view
    }

    private fun appendChat(message: String) {
        chatHistory.append("\n\n$message")
    }

    private fun extractBudget(text: String): Int? {
        val regex = Regex("\\d+")
        return regex.find(text)?.value?.toIntOrNull()
    }

    private fun generateSuggestions(budget: Int): String {
        if (budget < 50) {
            return "💸 الميزانية قليلة شوية، حاول تزودها شوية علشان نقدر نعمل خطة أسبوعية كويسة!"
        }

        val affordableItems = foodItems.filter { it.price <= budget }
        val possibleRecipes = recipes.filter { recipe ->
            recipe.ingredients.all { ing -> affordableItems.any { it.name == ing } }
        }

        if (possibleRecipes.size < 7) {
            val fallback = affordableItems.joinToString(", ") { it.name }
            return "📉 للأسف مش لاقي 7 وجبات كاملة بالميزانية دي، بس ممكن تشتري:\n$fallback\nوحاول تزود الميزانية لو تقدر."
        }

        val weekPlan = possibleRecipes.shuffled().take(7)
        val days = listOf("السبت", "الأحد", "الاثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة")

        val planText = weekPlan.mapIndexed { index, recipe ->
            "- ${days[index]}: ${recipe.name}"
        }.joinToString("\n")

        // 🛒 لستة مشتريات موحدة
        val allIngredients = weekPlan.flatMap { it.ingredients }.toSet()
        val shoppingList = allIngredients.joinToString("\n") { "- $it" }

        return """
        🍽️ **خطة أسبوع بناءً على ميزانيتك (${budget} جنيه):**
        $planText

        🧾 **لستة المشتريات للأسبوع:**
        $shoppingList
    """.trimIndent()
    }



    data class FoodItem(val name: String, val price: Int)
    data class Recipe(val name: String, val ingredients: List<String>)
}