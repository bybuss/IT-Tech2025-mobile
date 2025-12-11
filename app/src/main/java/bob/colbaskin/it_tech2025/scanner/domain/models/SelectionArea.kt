package bob.colbaskin.it_tech2025.scanner.domain.models

data class SelectionArea(
    val topLeft: PointF,
    val topRight: PointF,
    val bottomLeft: PointF,
    val bottomRight: PointF
) {
    data class PointF(val x: Float, val y: Float)
}
