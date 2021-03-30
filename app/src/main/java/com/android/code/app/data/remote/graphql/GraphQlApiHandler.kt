package com.android.code.app.data.remote.graphql

import android.util.Log
import com.apollographql.apollo.api.Mutation
import com.apollographql.apollo.api.Query
import com.apollographql.apollo.coroutines.toDeferred
import com.apollographql.apollo.exception.ApolloException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
/**
 * @AUTHOR Amandeep Singh
 * */
class GraphQlApiHandler private constructor() {

    /**
     * Method to make user login
     *
     * @param mutation input mutation
     * @param listener listener for sending response to calling page
     */
    companion object {
        val instance =
            GraphQlApiHandler()
    }

    fun <Q, L> getData(myQuery: Q, listener: L) where Q : Query<*, *, *>, L : GenericListener<Any> {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response = GraphQLHandler.apolloClient.query(myQuery).toDeferred().await()
                val assets = response.data()
                listener.onResult(assets!!)
            } catch (e: ApolloException) {
                // you will end up here if .await() throws, most likely due to a transport or parsing error
                listener.onError(e.message)
            } catch (e: NullPointerException) {
                // you will end up here if repositories!! throws above. This will happen if your server sends a response
                // with missing fields or errors
                listener.onError(e.message)
            } catch (e: Exception) {
                // you will end up here if repositories!! throws above. This will happen if your server sends a response
                // with missing fields or errors
                Log.d("error", e.message)
                listener.onError(e.message)

            }
        }
    }

    fun <M, L> postData(mutation: M, listener: L) where M : Mutation<*, *, *>, L : GenericListener<Any> {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response = GraphQLHandler.apolloClient.mutate(mutation).toDeferred().await()
                val assets = response.data()
                listener.onResult(assets!!)
            } catch (e: ApolloException) {
                // you will end up here if .await() throws, most likely due to a transport or parsing error
                listener.onError(e.message)
            } catch (e: NullPointerException) {
                // you will end up here if repositories!! throws above. This will happen if your server sends a response
                // with missing fields or errors
                listener.onError(e.message)

            } catch (e: Exception) {
                // you will end up here if repositories!! throws above. This will happen if your server sends a response
                // with missing fields or errors
                listener.onError(e.message)
            }
        }
    }

}