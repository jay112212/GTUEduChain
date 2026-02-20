package com.gtu.gtueduchain.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.gtu.gtueduchain.data.blockchain.BlockchainEngine
import com.gtu.gtueduchain.data.repository.DegreeRepositoryImpl
import com.gtu.gtueduchain.data.remote.FirestoreService
import com.gtu.gtueduchain.domain.usecase.*
import com.gtu.gtueduchain.ui.admin.AdminDashboardScreen
import com.gtu.gtueduchain.ui.admin.AdminLoginScreen
import com.gtu.gtueduchain.ui.home.HomeScreen
import com.gtu.gtueduchain.ui.student.CertificateScreen
import com.gtu.gtueduchain.ui.ledger.LedgerScreen
import com.gtu.gtueduchain.ui.common.BottomBar
import com.gtu.gtueduchain.viewmodel.*
import com.gtu.gtueduchain.navigation.Routes
import com.gtu.gtueduchain.ui.verification.VerificationScreen
import com.gtu.gtueduchain.viewmodel.AdminAuthViewModel
import com.gtu.gtueduchain.viewmodel.AdminViewModel


@Composable
fun NavGraph() {

    val navController = rememberNavController()

    // 🔗 Local Blockchain
    val engine = remember { BlockchainEngine() }

    // 🔥 Firestore Service
    val firestoreService = remember { FirestoreService() }

    // 🔥 Hybrid Repository (Local + Cloud)
    val repository = remember {
        DegreeRepositoryImpl(engine, firestoreService)
    }

    val issueUseCase = remember { IssueDegreeUseCase(repository) }
    val verifyUseCase = remember { VerifyDegreeUseCase(repository) }
    val getLedgerUseCase = remember { GetLedgerUseCase(repository) }
    val validateUseCase = remember { ValidateBlockchainUseCase(repository) }

    // 🔐 Admin Auth ViewModel (shared)
    val authVM: AdminAuthViewModel = viewModel()

    Scaffold(
        bottomBar = {
            BottomBar(navController = navController)
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = Routes.Home.route,
            modifier = Modifier.padding(padding)
        ) {

            composable(Routes.Home.route) {
                val vm: HomeViewModel = viewModel(
                    factory = ViewModelFactory { HomeViewModel(getLedgerUseCase) }
                )
                HomeScreen(navController, vm)
            }

            composable(Routes.Admin.route) {

                val authVM: AdminAuthViewModel = viewModel()

                if (authVM.isLoggedIn) {

                    val vm: AdminViewModel = viewModel(
                        factory = ViewModelFactory { AdminViewModel(issueUseCase) }
                    )

                    AdminDashboardScreen(
                        navController = navController,
                        viewModel = vm,
                        authViewModel = authVM
                    )

                } else {
                    AdminLoginScreen(viewModel = authVM)
                }
            }




            composable(Routes.Student.route) {
                val vm: StudentViewModel = viewModel(
                    factory = ViewModelFactory { StudentViewModel(verifyUseCase) }
                )
                CertificateScreen(navController, vm)
            }

            composable(Routes.Verify.route) {
                val vm: StudentViewModel = viewModel(
                    factory = ViewModelFactory { StudentViewModel(verifyUseCase) }
                )
                VerificationScreen(vm)   // ✅ USE NEW SCREEN
            }


            composable(Routes.Ledger.route) {
                val vm: LedgerViewModel = viewModel(
                    factory = ViewModelFactory {
                        LedgerViewModel(getLedgerUseCase, validateUseCase)
                    }
                )
                LedgerScreen(navController, vm)
            }
        }
    }
}
