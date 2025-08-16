package com.hfad.pokevault.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.hfad.pokevault.data.api.PokeApiService
import com.hfad.pokevault.data.api.PokemonListItem


class PokemonPagingSource(
    private val apiService: PokeApiService
) : PagingSource<Int, PokemonListItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PokemonListItem> {
        return try {
            val currentPage = params.key ?: 0   // offset
            val limit = params.loadSize

            // Get basic list
            val response = apiService.getPokemonList(limit = limit, offset = currentPage)

            // Enrich each item with its types from details endpoint
            val pokemons = response.results.map { item ->
                try {
                    val details = apiService.getPokemonDetails(item.name)
                    val types = details.types.map { it.type.name }
                    item.copy(types = types)
                } catch (e: Exception) {
                    // fallback to no types if details call fails
                    item
                }
            }

            val nextKey = if (pokemons.isEmpty()) null else currentPage + limit

            LoadResult.Page(
                data = pokemons,
                prevKey = if (currentPage == 0) null else currentPage - limit,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, PokemonListItem>): Int? {
        return state.anchorPosition?.let { anchorPos ->
            val page = state.closestPageToPosition(anchorPos)
            page?.prevKey?.plus(state.config.pageSize) ?: page?.nextKey?.minus(state.config.pageSize)
        }
    }
}
