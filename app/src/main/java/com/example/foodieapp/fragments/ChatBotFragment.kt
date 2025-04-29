package com.example.foodieapp.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodieapp.R

class ChatBotFragment : Fragment() {

    data class Ingredient(val name: String, val quantityPerPerson: Int)
    data class Recipe(val name: String, val ingredients: List<Ingredient>, val price: Int, val isVegetarian: Boolean)
    data class ChatMessage(val content: String, val isBotMessage: Boolean)

    private lateinit var budgetInput: EditText
    private lateinit var peopleInput: EditText
    private lateinit var generateButton: Button
    private lateinit var dietSpinner: Spinner
    private lateinit var recyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var viewPlanButton: Button

    private val messages = mutableListOf<ChatMessage>()

    private val recipes = listOf(
        Recipe("كشري", listOf(
            Ingredient("عدس", 100), Ingredient("رز", 100), Ingredient("بصل", 50),
            Ingredient("زيت", 20), Ingredient("ملح", 5)
        ), 30, true),
        Recipe("بيض بالطماطم", listOf(
            Ingredient("بيض", 2), Ingredient("طماطم", 50),
            Ingredient("زيت", 10), Ingredient("ملح", 5)
        ), 15, true),
        Recipe("مكرونة بالصلصة", listOf(
            Ingredient("مكرونة", 100), Ingredient("طماطم", 70),
            Ingredient("زيت", 10), Ingredient("ملح", 5)
        ), 25, true),
        Recipe("فول بالطماطم", listOf(
            Ingredient("فول", 100), Ingredient("طماطم", 50),
            Ingredient("زيت", 10), Ingredient("ملح", 5)
        ), 20, true),
        Recipe("بيض مقلي", listOf(
            Ingredient("بيض", 2), Ingredient("زيت", 10),
            Ingredient("ملح", 3)
        ), 10, true),
        Recipe("عيش بالجبنة", listOf(
            Ingredient("عيش", 1), Ingredient("جبنة", 50)
        ), 25, true),
        Recipe("مكرونة بالسجق", listOf(
            Ingredient("مكرونة", 100), Ingredient("سجق", 70),
            Ingredient("بصل", 30), Ingredient("زيت", 10),
            Ingredient("ملح", 5)
        ), 40, false),
        Recipe("رز باللحمة", listOf(
            Ingredient("رز", 100), Ingredient("لحمة", 100),
            Ingredient("بصل", 30), Ingredient("زيت", 10),
            Ingredient("ملح", 5)
        ), 60, false),
        Recipe("لحمة بالبصل", listOf(
            Ingredient("لحمة", 150), Ingredient("بصل", 50),
            Ingredient("زيت", 10), Ingredient("ملح", 5)
        ), 80, false),
        Recipe("كبسة بالفراخ", listOf(
            Ingredient("رز", 100), Ingredient("فراخ", 150),
            Ingredient("طماطم", 50), Ingredient("زيت", 10),
            Ingredient("ملح", 5)
        ), 85, false),
        Recipe("طاجن بطاطس باللحمة", listOf(
            Ingredient("لحمة", 150), Ingredient("بطاطس", 200),
            Ingredient("بصل", 50), Ingredient("زيت", 10),
            Ingredient("ملح", 5)
        ), 75, false),
        Recipe("مكرونة بالبشاميل", listOf(
            Ingredient("مكرونة", 100), Ingredient("لبن", 100),
            Ingredient("زبدة", 50), Ingredient("دقيق", 20),
            Ingredient("ملح", 5)
        ), 40, true),
        Recipe("فتة لحمة", listOf(
            Ingredient("لحمة", 200), Ingredient("عيش", 1),
            Ingredient("زيت", 10), Ingredient("ملح", 5)
        ), 70, false),
        Recipe("بيتزا خضار", listOf(
            Ingredient("دقيق", 100), Ingredient("جبنة", 50),
            Ingredient("طماطم", 50), Ingredient("زيت", 10)
        ), 35, true),
        Recipe("ملوخية بالأرز", listOf(
            Ingredient("ملوخية", 100), Ingredient("رز", 100),
            Ingredient("زيت", 10), Ingredient("ثوم", 20),
            Ingredient("ملح", 5)
        ), 30, true),
        Recipe("فطير مشلتت", listOf(
            Ingredient("دقيق", 200), Ingredient("سمنة", 50),
            Ingredient("ملح", 5)
        ), 45, true),
        Recipe("بط بالفريك", listOf(
            Ingredient("بط", 200), Ingredient("فريك", 150),
            Ingredient("بصل", 50), Ingredient("زيت", 10),
            Ingredient("ملح", 5)
        ), 95, false),
        Recipe("مندي لحم", listOf(
            Ingredient("رز", 150), Ingredient("لحمة", 200),
            Ingredient("بصل", 50), Ingredient("زيت", 10),
            Ingredient("ملح", 5)
        ), 90, false)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chatbot, container, false)

        budgetInput = view.findViewById(R.id.et_budget)
        peopleInput = view.findViewById(R.id.et_people)
        generateButton = view.findViewById(R.id.btn_generate)
        dietSpinner = view.findViewById(R.id.spinner_diet)
        recyclerView = view.findViewById(R.id.recyclerView)
        viewPlanButton = view.findViewById(R.id.btn_view_plan)

        setupRecyclerView()
        setupDietSpinner()
        setupButtons()

        return view
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        chatAdapter = ChatAdapter(messages)
        recyclerView.adapter = chatAdapter
    }

    private fun setupDietSpinner() {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.diet_types,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dietSpinner.adapter = adapter
        }
    }

    private fun setupButtons() {
        generateButton.setOnClickListener {
            val budget = budgetInput.text.toString().toIntOrNull() ?: 0
            val people = peopleInput.text.toString().toIntOrNull() ?: 1
            val isVegetarian = dietSpinner.selectedItem.toString() == "Vegeterian"

            if (budget <= 0 || people <= 0) {
                showToast("من فضلك أدخل ميزانية وعدد أفراد صحيحين")
                return@setOnClickListener
            }

            generateAndDisplayPlan(budget, people, isVegetarian)
        }

        viewPlanButton.setOnClickListener {
            viewSavedPlan()
        }
    }

    private fun generateAndDisplayPlan(budget: Int, people: Int, isVegetarian: Boolean) {
        val plan = generateWeeklyPlan(budget, people, isVegetarian)
        addMessageToChat(plan, isBotMessage = true)
        savePlan(plan, isVegetarian)
    }

    private fun generateWeeklyPlan(budget: Int, people: Int, isVegetarian: Boolean): String {
        val daysOfWeek = listOf("الأحد", "الإثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة", "السبت")
        val adjustedBudget = budget

        val filteredRecipes = if (isVegetarian) {
            recipes.filter { recipe ->
                recipe.isVegetarian && !containsMeat(recipe.ingredients)
            }.takeIf { it.isNotEmpty() }
                ?: return "لا توجد وصفات نباتية متاحة تناسب الميزانية"
        } else {
            recipes
        }

        val adjustedRecipes = filteredRecipes.map { recipe ->
            recipe.copy(
                ingredients = recipe.ingredients.map { ingredient ->
                    ingredient.copy(quantityPerPerson = ingredient.quantityPerPerson * people)
                },
                price = recipe.price * people
            )
        }

        val dailyBudget = adjustedBudget / 7
        val (affordable, expensive) = adjustedRecipes.partition { it.price <= dailyBudget }
        val weeklyPlan = mutableListOf<Recipe>()
        var remainingBudget = adjustedBudget

        val selectedRecipes = mutableSetOf<String>()
        val shuffledAffordable = affordable.shuffled()
        val shuffledExpensive = expensive.shuffled()

        for (i in 0 until 7) {
            val available = when {
                remainingBudget >= dailyBudget && shuffledAffordable.isNotEmpty() -> shuffledAffordable
                else -> shuffledExpensive.filter { it.price <= remainingBudget }
            }

            if (available.isEmpty()) break

            val recipe = available.firstOrNull { !selectedRecipes.contains(it.name) } ?: available.random()

            if (remainingBudget >= recipe.price) {
                weeklyPlan.add(recipe)
                selectedRecipes.add(recipe.name)
                remainingBudget -= recipe.price
            }
        }

        if (weeklyPlan.isEmpty()) {
            return "لا يمكن إنشاء خطة بهذه الميزانية لـ $people أشخاص"
        }

        val shoppingList = weeklyPlan.flatMap { recipe ->
            recipe.ingredients.map { ingredient ->
                ingredient.name to ingredient.quantityPerPerson
            }
        }.groupBy({ it.first }, { it.second })
            .mapValues { (_, values) -> values.sum() }

        val mealsByDay = weeklyPlan.mapIndexed { index, recipe ->
            "${daysOfWeek[index]}: ${recipe.name} (${recipe.price} جنيه لـ $people أشخاص)"
        }.joinToString("\n")

        return """
        🍽️ خطة الأسبوع (${if (isVegetarian) "نباتي" else "عادي"})
        الميزانية الإجمالية: ${adjustedBudget} جنيه ($people أشخاص)
        المتبقي: ${remainingBudget} جنيه

        الوجبات:
        $mealsByDay

        🛒 لستة المشتريات (لـ $people أشخاص):
        ${shoppingList.map { (name, qty) -> "- $name: $qty جرام" }.joinToString("\n")}
        """.trimIndent()
    }

    private fun containsMeat(ingredients: List<Ingredient>): Boolean {
        val meatKeywords = listOf("لحم", "لحمة", "سجق", "فراخ", "بط", "لحوم", "ستيك", "برجر")
        return ingredients.any { ing -> meatKeywords.any { ing.name.contains(it) } }
    }

    private fun savePlan(plan: String, isVegetarian: Boolean) {
        requireContext().getSharedPreferences("meal_plan", Context.MODE_PRIVATE)
            .edit()
            .putString("weekly_plan", plan)
            .putBoolean("is_vegetarian", isVegetarian)
            .apply()
    }

    private fun viewSavedPlan() {
        val weeklyPlan = requireContext()
            .getSharedPreferences("meal_plan", Context.MODE_PRIVATE)
            .getString("weekly_plan", "")

        if (!weeklyPlan.isNullOrEmpty()) {
            findNavController().navigate(R.id.action_chatBotFragment_to_weeklyPlanFragment)
        } else {
            showToast("من فضلك أنشئ خطة أولاً.")
        }
    }

    private fun addMessageToChat(message: String, isBotMessage: Boolean) {
        messages.add(ChatMessage(message, isBotMessage))
        chatAdapter.notifyItemInserted(messages.size - 1)
        recyclerView.scrollToPosition(messages.size - 1)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    class ChatAdapter(private val messages: List<ChatMessage>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_item, parent, false)
            return ChatViewHolder(view)
        }

        override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
            val message = messages[position]
            holder.messageTextView.text = message.content
            holder.messageTextView.setBackgroundResource(
                if (message.isBotMessage) R.drawable.bot_message_background
                else R.drawable.user_message_background
            )
        }

        override fun getItemCount(): Int = messages.size

        class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val messageTextView: TextView = itemView.findViewById(R.id.tv_message)
        }
    }
}