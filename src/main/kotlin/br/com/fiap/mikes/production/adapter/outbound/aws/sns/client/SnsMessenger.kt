package br.com.fiap.mikes.production.adapter.outbound.aws.sns.client

fun interface SnsMessenger {
    fun send(topicName: String, message: String)
}