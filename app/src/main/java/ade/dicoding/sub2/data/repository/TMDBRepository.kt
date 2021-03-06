package ade.dicoding.sub2.data.repository

import ade.dicoding.sub2.data.local.LocalRepository
import ade.dicoding.sub2.data.local.entity.MovieDetailEntity
import ade.dicoding.sub2.data.local.entity.MoviesEntity
import ade.dicoding.sub2.data.local.entity.TVDetailEntity
import ade.dicoding.sub2.data.local.entity.TiviesEntity
import ade.dicoding.sub2.data.model.MovieDetail
import ade.dicoding.sub2.data.model.Movies
import ade.dicoding.sub2.data.model.TVDetail
import ade.dicoding.sub2.data.model.Tivies
import ade.dicoding.sub2.data.remote.ApiResponse
import ade.dicoding.sub2.network.MovieService
import ade.dicoding.sub2.util.AppExecutors
import ade.dicoding.sub2.vo.Resource
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList

class TMDBRepository(
    private val localRepository: LocalRepository?,
    private val remoteRepository: MovieService,
    private val appExecutors: AppExecutors?
) :
    TMDBDataSource {

    override fun movies(page: Int?): LiveData<Resource<List<MoviesEntity>>>? {
        return object : NetworkBoundResource<List<MoviesEntity>, Movies>(appExecutors) {
            override fun loadFromDB(): LiveData<List<MoviesEntity>>? {
                return localRepository?.getAllMovie(page)
            }

            override fun shouldFetch(data: List<MoviesEntity>?): Boolean? {
                return data.isNullOrEmpty()
            }

            override fun createCall(): LiveData<ApiResponse<Movies>> {
                Log.e("page", page.toString())
                return remoteRepository.getMovie(page)
            }

            override fun saveCallResult(data: Movies?) {
                val movieList = mutableListOf<MoviesEntity>()
                data?.results?.forEach { result: Movies.Result? ->
                    result?.apply {
                        val moviesEntity =
                            MoviesEntity(
                                popularity,
                                posterPath,
                                data.page,
                                id,
                                backdropPath,
                                title,
                                overview,
                                releaseDate,
                                0
                            )
                        movieList.add(moviesEntity)
                    }
                }
                localRepository?.insertMovies(movieList)
            }

            override fun onFetchFailed(message: String?) {
                Log.e("moviesFetchFailed", "$message")
            }
        }.asLiveData()
    }

    override fun movies(key: String?): LiveData<ApiResponse<Movies>> {
        return remoteRepository.getMovie(key)
    }

    override fun favMovies(sort: String): LiveData<Resource<PagedList<MovieDetailEntity>>> {
        return object :
            NetworkBoundResource<PagedList<MovieDetailEntity>, MovieDetailEntity?>(appExecutors) {
            override fun loadFromDB(): LiveData<PagedList<MovieDetailEntity>>? {
                return localRepository?.getAllMovieDetail(sort)?.let {
                    LivePagedListBuilder<Int?, MovieDetailEntity>(
                        it,
                        20
                    ).build()
                }
            }

            override fun shouldFetch(data: PagedList<MovieDetailEntity>?) = false

            override fun createCall(): LiveData<ApiResponse<MovieDetailEntity?>>? = null

            override fun saveCallResult(data: MovieDetailEntity?) {
            }

            override fun onFetchFailed(message: String?) {
                Log.e("moviesfavFetchFailed", "$message")
            }
        }.asLiveData()
    }

    override fun favTivies(sort: String): LiveData<Resource<PagedList<TVDetailEntity>>> {
        return object :
            NetworkBoundResource<PagedList<TVDetailEntity>, TVDetailEntity?>(appExecutors) {
            override fun loadFromDB(): LiveData<PagedList<TVDetailEntity>>? {
                return localRepository?.getAllTVDetail(sort)?.let {
                    LivePagedListBuilder<Int?, TVDetailEntity>(
                        it,
                        20
                    ).build()
                }
            }

            override fun shouldFetch(data: PagedList<TVDetailEntity>?) = false

            override fun createCall(): LiveData<ApiResponse<TVDetailEntity?>>? = null

            override fun saveCallResult(data: TVDetailEntity?) {
            }

            override fun onFetchFailed(message: String?) {
                Log.e("tiviesFavFetchFailed", "$message")
            }
        }.asLiveData()
    }

    override fun tVShow(): LiveData<Resource<List<TiviesEntity>>>? {
        return object : NetworkBoundResource<List<TiviesEntity>, Tivies>(appExecutors) {
            override fun loadFromDB(): LiveData<List<TiviesEntity>>? {
                return localRepository?.getAllTivi()
            }

            override fun shouldFetch(data: List<TiviesEntity>?): Boolean {
                return data.isNullOrEmpty()
            }

            override fun createCall(): LiveData<ApiResponse<Tivies>> {
                return remoteRepository.getTV()
            }

            override fun saveCallResult(data: Tivies?) {
                data?.results?.forEach { result: Tivies.Result? ->
                    result?.apply {
                        val entity =
                            TiviesEntity(
                                title,
                                popularity,
                                firstAirDate,
                                backdropPath,
                                id,
                                overview,
                                posterPath,
                                0
                            )
                        localRepository?.insertTivies(entity)
                    }
                }

            }
        }.asLiveData()
    }

    override fun movieDetail(id: Int): LiveData<Resource<MovieDetailEntity>> {
        return object : NetworkBoundResource<MovieDetailEntity, MovieDetail>(appExecutors) {
            override fun loadFromDB(): LiveData<MovieDetailEntity>? {
                return localRepository?.getMovieDetail(id)
            }

            override fun shouldFetch(data: MovieDetailEntity?): Boolean {
                return data == null
            }

            override fun createCall(): LiveData<ApiResponse<MovieDetail>> {
                return remoteRepository.getMovieDetail(id)
            }

            override fun saveCallResult(data: MovieDetail?) {
                if (localRepository != null) {
                    data?.apply {
                        var genre = ""
                        genres?.forEach {
                            genre = it?.name + " "
                        }
                        val movieDetailEntity = MovieDetailEntity(
                            genre,
                            homepage,
                            id,
                            title,
                            overview,
                            popularity,
                            posterPath,
                            releaseDate,
                            status,
                            tagline,
                            0
                        )
                        localRepository.insertMovieDetail(movieDetailEntity)
                    }

                }
            }

            override fun onFetchFailed(message: String?) {
                Log.e("moviesDtlFetchFailed", "$message")
            }
        }.asLiveData()
    }

    override fun tVDetail(id: Int): LiveData<Resource<TVDetailEntity>> {
        return object : NetworkBoundResource<TVDetailEntity, TVDetail>(appExecutors) {
            override fun loadFromDB(): LiveData<TVDetailEntity>? {
                return localRepository?.getTVDetail(id)
            }

            override fun shouldFetch(data: TVDetailEntity?): Boolean {
                return data == null
            }

            override fun createCall(): LiveData<ApiResponse<TVDetail>> {
                return remoteRepository.getTVDetail(id)
            }

            override fun saveCallResult(data: TVDetail?) {
                if (localRepository != null) {
                    data?.apply {
                        var genre = ""
                        genres?.forEach {
                            genre = it?.name + " "
                        }

                        var creator = ""
                        createdBy?.forEach {
                            creator = it?.name + " "
                        }
                        val entity = TVDetailEntity(
                            creator,
                            firstAirDate,
                            genre,
                            homepage,
                            id,
                            lastAirDate,
                            title,
                            overview,
                            popularity,
                            posterPath,
                            status,
                            0
                        )
                        localRepository.insertTVDetail(entity)
                    }

                }
            }

            override fun onFetchFailed(message: String?) {
                Log.e("tvDtlFetchFailed", "$message")
            }
        }.asLiveData()
    }

    override fun setFavMovie(movieDetail: MovieDetailEntity) {
        val runnable = Runnable { localRepository?.setFavMovie(movieDetail) }
        appExecutors?.diskIO()?.execute(runnable)
    }

    override fun setFavTV(tvDetail: TVDetailEntity) {
        val runnable = Runnable { localRepository?.setFavTV(tvDetail) }
        appExecutors?.diskIO()?.execute(runnable)
    }

    companion object {
        @Volatile
        private var INSTANCE: TMDBRepository? = null

        fun getInstance(
            localRepository: LocalRepository?,
            remoteData: MovieService,
            appExecutors: AppExecutors?
        ): TMDBRepository? {
            if (INSTANCE == null) {
                synchronized(TMDBRepository::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = TMDBRepository(localRepository, remoteData, appExecutors)
                    }
                }
            }
            return INSTANCE
        }
    }
}