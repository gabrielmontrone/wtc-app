package br.com.fiap.wtcapp.ui.campanhas

import br.com.fiap.wtcapp.domain.model.Campaign

data class CampanhasUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val campaigns: List<Campaign> = emptyList(),
) {
    val totalCampaigns: Int get() = campaigns.size

    val totalSends: Long get() = campaigns.sumOf { it.totalSends }

    val totalResponses: Long get() = campaigns.sumOf { it.responseCount }

    /** Aggregate success rate across all campaigns, as a 0–100 percentage. */
    val successRate: Int
        get() {
            val sends = totalSends
            if (sends == 0L) return 0
            return (campaigns.sumOf { it.successSends } * PERCENT / sends).toInt()
        }

    private companion object {
        const val PERCENT = 100
    }
}
