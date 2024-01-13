package br.com.fiap.mikes.production.adapter.outbound.aws.sns.client

fun interface SnsMessenger<M> {
    fun send(topicName: String, message: M)
}