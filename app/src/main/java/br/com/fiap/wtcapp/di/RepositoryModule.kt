package br.com.fiap.wtcapp.di

import br.com.fiap.wtcapp.data.repository.AuthRepositoryImpl
import br.com.fiap.wtcapp.data.repository.CampaignRepositoryImpl
import br.com.fiap.wtcapp.data.repository.ConversationRepositoryImpl
import br.com.fiap.wtcapp.data.repository.CustomerRepositoryImpl
import br.com.fiap.wtcapp.data.repository.MessageRepositoryImpl
import br.com.fiap.wtcapp.data.repository.SegmentRepositoryImpl
import br.com.fiap.wtcapp.domain.repository.AuthRepository
import br.com.fiap.wtcapp.domain.repository.CampaignRepository
import br.com.fiap.wtcapp.domain.repository.ConversationRepository
import br.com.fiap.wtcapp.domain.repository.CustomerRepository
import br.com.fiap.wtcapp.domain.repository.MessageRepository
import br.com.fiap.wtcapp.domain.repository.SegmentRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindCustomerRepository(impl: CustomerRepositoryImpl): CustomerRepository

    @Binds
    @Singleton
    abstract fun bindSegmentRepository(impl: SegmentRepositoryImpl): SegmentRepository

    @Binds
    @Singleton
    abstract fun bindCampaignRepository(impl: CampaignRepositoryImpl): CampaignRepository

    @Binds
    @Singleton
    abstract fun bindConversationRepository(impl: ConversationRepositoryImpl): ConversationRepository

    @Binds
    @Singleton
    abstract fun bindMessageRepository(impl: MessageRepositoryImpl): MessageRepository
}
