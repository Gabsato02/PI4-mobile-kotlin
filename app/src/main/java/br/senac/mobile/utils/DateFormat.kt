package br.senac.mobile.utils

fun formatDate(date: String): String {
    val dateFormat = date.split(" ")[0]
    val timeFormat = date.split(" ")[1]

    val dateSplited = dateFormat.split("-")
    return "${dateSplited[2]}/${dateSplited[1]}/${dateSplited[0]}"
}