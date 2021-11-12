package br.senac.mobile.utils

fun getResponseMessage(statusCode: Int): String {
    return when(statusCode) {
        200 -> "Operação realizada com sucesso."
        201 -> "Operação realizada com sucesso."
        400 -> "Parâmetros incorretos. Verifique as informações fornecidas e tente novamente."
        402 -> "Não autorizado. Tente novamente ou contate um administrador."
        404 -> "Não foram encontrados resultados."
        else -> "Não foi possível executar a operação."
    }
}