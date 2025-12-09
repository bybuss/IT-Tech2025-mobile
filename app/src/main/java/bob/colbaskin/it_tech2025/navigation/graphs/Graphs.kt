package bob.colbaskin.it_tech2025.navigation.graphs

import kotlinx.serialization.Serializable

interface Graphs {

    @Serializable
    data object Main: Graphs

    @Serializable
    data object Onboarding: Graphs

    @Serializable
    data object Detailed: Graphs
}
