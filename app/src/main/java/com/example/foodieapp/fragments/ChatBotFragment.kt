package com.example.foodieapp.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodieapp.R

class ChatBotFragment : Fragment() {

    data class Ingredient(val name: String, val quantityPerPerson: Int)
    data class Recipe(val name: String, val ingredients: List<Ingredient>, val price: Int)
    data class FoodItem(val name: String, val price: Int)
    data class ChatMessage(val content: String, val isBotMessage: Boolean)

    private lateinit var budgetInput: EditText
    private lateinit var peopleInput: EditText
    private lateinit var generateButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter

    private val foodItems = listOf(
        FoodItem("رز", 20), FoodItem("مكرونة", 15), FoodItem("عدس", 10),
        FoodItem("بيض", 18), FoodItem("فول", 10), FoodItem("جبنة", 12),
        FoodItem("عيش", 5), FoodItem("سجق", 40), FoodItem("لحمة", 70),
        FoodItem("بصل", 5), FoodItem("طماطم", 8), FoodItem("زيت", 10),
        FoodItem("ملح", 2), FoodItem("سمنة", 10)
    )

    private val recipes = listOf(
        Recipe("كشري", listOf(
            Ingredient("عدس", 100), Ingredient("رز", 100), Ingredient("بصل", 50),
            Ingredient("زيت", 20), Ingredient("ملح", 5)
        ), 30),
        Recipe("بيض بالطماطم", listOf(
            Ingredient("بيض", 2), Ingredient("طماطم", 50), Ingredient("زيت", 10), Ingredient("ملح", 5)
        ), 15),
        Recipe("مكرونة بالصلصة", listOf(
            Ingredient("مكرونة", 100), Ingredient("طماطم", 70), Ingredient("زيت", 10), Ingredient("ملح", 5)
        ), 25),
        Recipe("فول بالطماطم", listOf(
            Ingredient("فول", 100), Ingredient("طماطم", 50), Ingredient("زيت", 10), Ingredient("ملح", 5)
        ), 20),
        Recipe("بيض مقلي", listOf(
            Ingredient("بيض", 2), Ingredient("زيت", 10), Ingredient("ملح", 3)
        ), 10),
        Recipe("عيش بالجبنة", listOf(
            Ingredient("عيش", 1), Ingredient("جبنة", 50)
        ), 25),
        Recipe("مكرونة بالسجق", listOf(
            Ingredient("مكرونة", 100), Ingredient("سجق", 70), Ingredient("بصل", 30), Ingredient("زيت", 10), Ingredient("ملح", 5)
        ), 40),
        Recipe("رز باللحمة", listOf(
            Ingredient("رز", 100), Ingredient("لحمة", 100), Ingredient("بصل", 30), Ingredient("زيت", 10), Ingredient("ملح", 5)
        ), 60),
        Recipe("لحمة بالبصل", listOf(
            Ingredient("لحمة", 150), Ingredient("بصل", 50), Ingredient("زيت", 10), Ingredient("ملح", 5)
        ), 80),
        Recipe("كبسة بالفراخ", listOf(
            Ingredient("رز", 100), Ingredient("فراخ", 150), Ingredient("طماطم", 50), Ingredient("زيت", 10), Ingredient("ملح", 5)
        ), 85),
        Recipe("طاجن بطاطس باللحمة", listOf(
            Ingredient("لحمة", 150), Ingredient("بطاطس", 200), Ingredient("بصل", 50), Ingredient("زيت", 10), Ingredient("ملح", 5)
        ), 75),
        Recipe("مكرونة بالبشاميل", listOf(
            Ingredient("مكرونة", 100), Ingredient("لبن", 100), Ingredient("زبدة", 50), Ingredient("دقيق", 20), Ingredient("ملح", 5)
        ), 40),
        Recipe("فتة لحمة", listOf(
            Ingredient("لحمة", 200), Ingredient("عيش", 1), Ingredient("زيت", 10), Ingredient("ملح", 5)
        ), 70)
    )

    private val messages = mutableListOf<ChatMessage>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chatbot, container, false)

        budgetInput = view.findViewById(R.id.et_budget)
        peopleInput = view.findViewById(R.id.et_people)
        generateButton = view.findViewById(R.id.btn_generate)
        recyclerView = view.findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        chatAdapter = ChatAdapter(messages)
        recyclerView.adapter = chatAdapter

        generateButton.setOnClickListener {
            val budgetStr = budgetInput.text.toString().trim()
            val peopleStr = peopleInput.text.toString().trim()

            if (budgetStr.isEmpty() || peopleStr.isEmpty()) {
                Toast.makeText(requireContext(), "من فضلك أدخل الميزانية وعدد الأفراد", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val budget = budgetStr.toIntOrNull()
            val people = peopleStr.toIntOrNull()

            if (budget == null || budget <= 0) {
                Toast.makeText(requireContext(), "أدخل ميزانية صحيحة (مثلاً 100)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (people == null || people <= 0) {
                Toast.makeText(requireContext(), "أدخل عدد أفراد صحيح (مثلاً 2)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val output = generateWeeklyPlan(budget, people)
            addMessageToChat("خطة الأسبوع:", true)
            addMessageToChat(output, true)
            savePlanToPrefs(requireContext(), output)
        }


        return view
    }

    private fun generateWeeklyPlan(budget: Int, people: Int): String {
        val dailyBudget = budget / 7
        val weeklyPlan = mutableListOf<Recipe>()
        var remainingBudget = budget

        val expensiveRecipes = recipes.filter { it.price > dailyBudget }
        val affordableRecipes = recipes.filter { it.price <= dailyBudget }

        for (i in 0 until 7) {
            if (i < expensiveRecipes.size && remainingBudget >= expensiveRecipes[i].price) {
                weeklyPlan.add(expensiveRecipes[i])
                remainingBudget -= expensiveRecipes[i].price
            } else {
                val selectedRecipe = affordableRecipes.random()
                weeklyPlan.add(selectedRecipe)
                remainingBudget -= selectedRecipe.price
            }
        }

        val shoppingMap = mutableMapOf<String, Int>()
        weeklyPlan.forEach { recipe ->
            recipe.ingredients.forEach { ingredient ->
                val total = ingredient.quantityPerPerson * people
                shoppingMap[ingredient.name] = shoppingMap.getOrDefault(ingredient.name, 0) + total
            }
        }

        val weekPlanText = weeklyPlan.mapIndexed { index, recipe ->
            "- يوم ${index + 1}: ${recipe.name} - (${recipe.price} جنيه)"
        }.joinToString("\n")

        val shoppingListText = shoppingMap.entries.joinToString("\n") {
            "- ${it.key}: ${it.value} جرام/عدد"
        }

        return """
            🍽️ **خطة الأسبوع (${budget} جنيه - ${people} أفراد):**
            $weekPlanText

            🧾 **لستة المشتريات:**
            $shoppingListText
        """.trimIndent()
    }

    private fun addMessageToChat(message: String, isBotMessage: Boolean) {
        messages.add(ChatMessage(message, isBotMessage))
        chatAdapter.notifyItemInserted(messages.size - 1)
        recyclerView.scrollToPosition(messages.size - 1)
    }

    private fun savePlanToPrefs(context: Context, plan: String) {
        val prefs = context.getSharedPreferences("meal_plan", Context.MODE_PRIVATE)
        prefs.edit().putString("weekly_plan", plan).apply()
    }
}

class ChatAdapter(private val messages: List<ChatBotFragment.ChatMessage>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_item, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messages[position]
        holder.messageTextView.text = message.content

        // Set different background color based on sender (bot/user)
        if (message.isBotMessage) {
            holder.messageTextView.setBackgroundResource(R.drawable.bot_message_background)
        } else {
            holder.messageTextView.setBackgroundResource(R.drawable.user_message_background)
        }
    }

    override fun getItemCount(): Int = messages.size

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageTextView: TextView = itemView.findViewById(R.id.tv_message)
    }
}
