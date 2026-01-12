package com.example.pokemonvizor.data

import com.example.pokemonvizor.domain.AnimalInfo
import com.example.pokemonvizor.domain.ArgbColor
import com.example.pokemonvizor.domain.PokemonEntry
import com.example.pokemonvizor.domain.PokemonPrimitiveModelSpec
import com.example.pokemonvizor.domain.PokemonRepository
import com.example.pokemonvizor.domain.Primitive
import com.example.pokemonvizor.domain.PrimitiveType
import com.example.pokemonvizor.domain.Vector3
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalPokemonRepository @Inject constructor() : PokemonRepository {
    private val animals = listOf(
        AnimalInfo(
            id = "deer",
            displayName = "Deer",
            description = "Graceful herbivore often found grazing near wooded areas.",
            funFacts = listOf("Antlers are shed annually.", "Excellent low-light vision."),
            wikipediaUrl = "https://en.wikipedia.org/wiki/Deer"
        ),
        AnimalInfo(
            id = "goat",
            displayName = "Goat",
            description = "Curious climber known for balancing on rocky terrain.",
            funFacts = listOf("Goats have rectangular pupils.", "They are social herd animals."),
            wikipediaUrl = "https://en.wikipedia.org/wiki/Goat"
        ),
        AnimalInfo(
            id = "rabbit",
            displayName = "Rabbit",
            description = "Small, quick mammal with long ears and powerful hind legs.",
            funFacts = listOf("Rabbits can rotate their ears 270 degrees.", "They thump to warn others."),
            wikipediaUrl = "https://en.wikipedia.org/wiki/Rabbit"
        ),
        AnimalInfo(
            id = "peacock",
            displayName = "Peacock",
            description = "Colorful bird famous for its iridescent tail display.",
            funFacts = listOf("Only males have the bright plumage.", "Tail feathers are called a train."),
            wikipediaUrl = "https://en.wikipedia.org/wiki/Peafowl"
        ),
        AnimalInfo(
            id = "boar",
            displayName = "Boar",
            description = "Strong wild pig with a sturdy body and tusks.",
            funFacts = listOf("Boars have excellent sense of smell.", "They can run surprisingly fast."),
            wikipediaUrl = "https://en.wikipedia.org/wiki/Wild_boar"
        ),
        AnimalInfo(
            id = "lynx",
            displayName = "Lynx",
            description = "Stealthy wild cat with tufted ears and sharp senses.",
            funFacts = listOf("Tufted ears help detect prey.", "Snowshoe-like paws aid in snow."),
            wikipediaUrl = "https://en.wikipedia.org/wiki/Lynx"
        ),
        AnimalInfo(
            id = "pony",
            displayName = "Pony",
            description = "Compact, sturdy horse breed with a friendly demeanor.",
            funFacts = listOf("Ponies are strong for their size.", "They have thick manes."),
            wikipediaUrl = "https://en.wikipedia.org/wiki/Pony"
        ),
        AnimalInfo(
            id = "bison",
            displayName = "Bison",
            description = "Massive grazing mammal with a thick mane and hump.",
            funFacts = listOf("Bison can sprint up to 35 mph.", "They live in herds."),
            wikipediaUrl = "https://en.wikipedia.org/wiki/Bison"
        )
    )

    private val pokemonEntries = listOf(
        PokemonEntry("deer", "springle", "Springle", "A nimble forest sprite with antler sparks."),
        PokemonEntry("goat", "cliffang", "Cliffang", "Climbs cliffs and charges with rocky horns."),
        PokemonEntry("rabbit", "whiskit", "Whiskit", "Dashes between burrows leaving a glowing trail."),
        PokemonEntry("peacock", "plumaria", "Plumaria", "Fans dazzling feathers to mesmerize opponents."),
        PokemonEntry("boar", "tuskwild", "Tuskwild", "Rams through thickets with iron tusks."),
        PokemonEntry("lynx", "nightpelt", "Nightpelt", "Silent hunter that blends into moonlit snow."),
        PokemonEntry("pony", "breezette", "Breezette", "A playful steed that rides the wind."),
        PokemonEntry("bison", "thunderhoof", "Thunderhoof", "Stomps the ground to summon rumbling shocks.")
    )

    private val pokemonSpecs = listOf(
        PokemonPrimitiveModelSpec(
            pokemonId = "springle",
            primitives = listOf(
                Primitive("body", PrimitiveType.BOX, Vector3(0.8f, 0.6f, 0.4f), Vector3(0f, 0f, 0f), Vector3(0f, 0f, 0f), ArgbColor(1f, 0.6f, 0.8f, 0.3f)),
                Primitive("head", PrimitiveType.SPHERE, Vector3(0.35f, 0.35f, 0.35f), Vector3(0f, 0.55f, 0f), Vector3(0f, 0f, 0f), ArgbColor(1f, 0.9f, 0.9f, 0.7f)),
                Primitive("antler_left", PrimitiveType.CONE, Vector3(0.1f, 0.35f, 0.1f), Vector3(-0.2f, 0.9f, 0f), Vector3(-20f, 0f, 20f), ArgbColor(1f, 0.4f, 0.3f, 0.1f)),
                Primitive("antler_right", PrimitiveType.CONE, Vector3(0.1f, 0.35f, 0.1f), Vector3(0.2f, 0.9f, 0f), Vector3(-20f, 0f, -20f), ArgbColor(1f, 0.4f, 0.3f, 0.1f))
            )
        ),
        PokemonPrimitiveModelSpec(
            pokemonId = "cliffang",
            primitives = listOf(
                Primitive("body", PrimitiveType.CYLINDER, Vector3(0.5f, 0.7f, 0.5f), Vector3(0f, 0f, 0f), Vector3(0f, 0f, 0f), ArgbColor(1f, 0.5f, 0.4f, 0.3f)),
                Primitive("head", PrimitiveType.SPHERE, Vector3(0.35f, 0.35f, 0.35f), Vector3(0f, 0.6f, 0f), Vector3(0f, 0f, 0f), ArgbColor(1f, 0.8f, 0.7f, 0.6f)),
                Primitive("horn_left", PrimitiveType.CONE, Vector3(0.12f, 0.4f, 0.12f), Vector3(-0.25f, 0.9f, 0f), Vector3(-10f, 0f, 30f), ArgbColor(1f, 0.2f, 0.2f, 0.2f)),
                Primitive("horn_right", PrimitiveType.CONE, Vector3(0.12f, 0.4f, 0.12f), Vector3(0.25f, 0.9f, 0f), Vector3(-10f, 0f, -30f), ArgbColor(1f, 0.2f, 0.2f, 0.2f))
            )
        ),
        PokemonPrimitiveModelSpec(
            pokemonId = "whiskit",
            primitives = listOf(
                Primitive("body", PrimitiveType.SPHERE, Vector3(0.45f, 0.45f, 0.45f), Vector3(0f, 0f, 0f), Vector3(0f, 0f, 0f), ArgbColor(1f, 0.7f, 0.7f, 0.9f)),
                Primitive("ears_left", PrimitiveType.CONE, Vector3(0.1f, 0.35f, 0.1f), Vector3(-0.2f, 0.6f, 0f), Vector3(-10f, 0f, 15f), ArgbColor(1f, 0.9f, 0.8f, 0.9f)),
                Primitive("ears_right", PrimitiveType.CONE, Vector3(0.1f, 0.35f, 0.1f), Vector3(0.2f, 0.6f, 0f), Vector3(-10f, 0f, -15f), ArgbColor(1f, 0.9f, 0.8f, 0.9f))
            )
        ),
        PokemonPrimitiveModelSpec(
            pokemonId = "plumaria",
            primitives = listOf(
                Primitive("body", PrimitiveType.CYLINDER, Vector3(0.35f, 0.8f, 0.35f), Vector3(0f, -0.1f, 0f), Vector3(0f, 0f, 0f), ArgbColor(1f, 0.2f, 0.6f, 0.9f)),
                Primitive("head", PrimitiveType.SPHERE, Vector3(0.25f, 0.25f, 0.25f), Vector3(0f, 0.6f, 0f), Vector3(0f, 0f, 0f), ArgbColor(1f, 0.95f, 0.9f, 0.4f)),
                Primitive("tail", PrimitiveType.CONE, Vector3(0.15f, 0.6f, 0.15f), Vector3(0f, -0.1f, -0.4f), Vector3(20f, 0f, 0f), ArgbColor(1f, 0.2f, 0.9f, 0.6f))
            )
        ),
        PokemonPrimitiveModelSpec(
            pokemonId = "tuskwild",
            primitives = listOf(
                Primitive("body", PrimitiveType.BOX, Vector3(0.9f, 0.6f, 0.5f), Vector3(0f, 0f, 0f), Vector3(0f, 0f, 0f), ArgbColor(1f, 0.4f, 0.2f, 0.2f)),
                Primitive("head", PrimitiveType.BOX, Vector3(0.4f, 0.35f, 0.3f), Vector3(0f, 0.35f, 0.35f), Vector3(0f, 0f, 0f), ArgbColor(1f, 0.5f, 0.3f, 0.25f)),
                Primitive("tusk_left", PrimitiveType.CYLINDER, Vector3(0.05f, 0.3f, 0.05f), Vector3(-0.15f, 0.25f, 0.6f), Vector3(90f, 0f, 0f), ArgbColor(1f, 0.9f, 0.9f, 0.9f)),
                Primitive("tusk_right", PrimitiveType.CYLINDER, Vector3(0.05f, 0.3f, 0.05f), Vector3(0.15f, 0.25f, 0.6f), Vector3(90f, 0f, 0f), ArgbColor(1f, 0.9f, 0.9f, 0.9f))
            )
        ),
        PokemonPrimitiveModelSpec(
            pokemonId = "nightpelt",
            primitives = listOf(
                Primitive("body", PrimitiveType.SPHERE, Vector3(0.5f, 0.5f, 0.5f), Vector3(0f, 0f, 0f), Vector3(0f, 0f, 0f), ArgbColor(1f, 0.2f, 0.2f, 0.3f)),
                Primitive("ears_left", PrimitiveType.CONE, Vector3(0.08f, 0.3f, 0.08f), Vector3(-0.2f, 0.55f, 0f), Vector3(-15f, 0f, 10f), ArgbColor(1f, 0.3f, 0.3f, 0.4f)),
                Primitive("ears_right", PrimitiveType.CONE, Vector3(0.08f, 0.3f, 0.08f), Vector3(0.2f, 0.55f, 0f), Vector3(-15f, 0f, -10f), ArgbColor(1f, 0.3f, 0.3f, 0.4f))
            )
        ),
        PokemonPrimitiveModelSpec(
            pokemonId = "breezette",
            primitives = listOf(
                Primitive("body", PrimitiveType.BOX, Vector3(0.7f, 0.4f, 0.35f), Vector3(0f, 0f, 0f), Vector3(0f, 0f, 0f), ArgbColor(1f, 0.7f, 0.8f, 0.95f)),
                Primitive("head", PrimitiveType.SPHERE, Vector3(0.3f, 0.3f, 0.3f), Vector3(0f, 0.35f, 0.2f), Vector3(0f, 0f, 0f), ArgbColor(1f, 0.9f, 0.9f, 0.95f)),
                Primitive("mane", PrimitiveType.CONE, Vector3(0.15f, 0.45f, 0.15f), Vector3(0f, 0.4f, -0.1f), Vector3(60f, 0f, 0f), ArgbColor(1f, 0.4f, 0.6f, 0.9f))
            )
        ),
        PokemonPrimitiveModelSpec(
            pokemonId = "thunderhoof",
            primitives = listOf(
                Primitive("body", PrimitiveType.CYLINDER, Vector3(0.65f, 0.7f, 0.65f), Vector3(0f, 0f, 0f), Vector3(0f, 0f, 0f), ArgbColor(1f, 0.3f, 0.25f, 0.2f)),
                Primitive("head", PrimitiveType.BOX, Vector3(0.35f, 0.3f, 0.4f), Vector3(0f, 0.45f, 0.35f), Vector3(0f, 0f, 0f), ArgbColor(1f, 0.4f, 0.3f, 0.25f)),
                Primitive("horn", PrimitiveType.CONE, Vector3(0.15f, 0.45f, 0.15f), Vector3(0f, 0.8f, 0.2f), Vector3(-20f, 0f, 0f), ArgbColor(1f, 0.9f, 0.8f, 0.5f))
            )
        )
    )

    override fun getAnimalInfo(animalId: String): AnimalInfo? {
        return animals.firstOrNull { it.id == animalId }
    }

    override fun getPokemonEntry(animalId: String): PokemonEntry? {
        return pokemonEntries.firstOrNull { it.animalId == animalId }
    }

    override fun getPokemonSpec(pokemonId: String): PokemonPrimitiveModelSpec? {
        return pokemonSpecs.firstOrNull { it.pokemonId == pokemonId }
    }

    override fun getAllAnimalIds(): List<String> = animals.map { it.id }
}
