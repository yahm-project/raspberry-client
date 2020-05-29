package it.unibo.yahm.client.entities

data class RoadIssueClassificationResult(
        val result: ObstacleType,
        val values: Map<ObstacleType, Float>
)
