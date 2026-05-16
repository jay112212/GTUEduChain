package com.gtu.gtueduchain.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gtu.gtueduchain.data.remote.FirestoreService
import com.gtu.gtueduchain.data.repository.DegreeRepositoryImpl
import com.gtu.gtueduchain.domain.usecase.IssueDegreeUseCase
import com.gtu.gtueduchain.domain.usecase.VerifyDegreeUseCase
import com.gtu.gtueduchain.ui.admin.AdminDashboardScreen
import com.gtu.gtueduchain.ui.admin.AdminLoginScreen
import com.gtu.gtueduchain.ui.admin.IssueDegreeScreen
import com.gtu.gtueduchain.ui.common.BottomBar
import com.gtu.gtueduchain.ui.home.HomeScreen
import com.gtu.gtueduchain.ui.ledger.BlockDetailScreen
import com.gtu.gtueduchain.ui.ledger.LedgerScreen
import com.gtu.gtueduchain.ui.student.CertificateScreen
import com.gtu.gtueduchain.ui.verification.VerificationScreen
import com.gtu.gtueduchain.viewmodel.AdminAuthViewModel
import com.gtu.gtueduchain.viewmodel.AdminViewModel
import com.gtu.gtueduchain.viewmodel.HomeViewModel
import com.gtu.gtueduchain.viewmodel.LedgerViewModel
import com.gtu.gtueduchain.viewmodel.StudentViewModel
import com.gtu.gtueduchain.viewmodel.ViewModelFactory

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val firestoreService = remember { FirestoreService() }
    val repository = remember { DegreeRepositoryImpl(firestoreService) }

    val issueUseCase = remember { IssueDegreeUseCase(repository) }
    val verifyUseCase = remember { VerifyDegreeUseCase(repository) }

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
                    factory = ViewModelFactory {
                        HomeViewModel(repository)
                    }
                )
                HomeScreen(navController, vm)
            }

            composable(Routes.Admin.route) {
                val authVM: AdminAuthViewModel = viewModel()

                if (authVM.isLoggedIn) {
                    val vm: AdminViewModel = viewModel(
                        factory = ViewModelFactory {
                            AdminViewModel(issueUseCase)
                        }
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

            composable(Routes.IssueDegree.route) {
                val vm: AdminViewModel = viewModel(
                    factory = ViewModelFactory {
                        AdminViewModel(issueUseCase)
                    }
                )

                IssueDegreeScreen(
                    navController = navController,
                    viewModel = vm
                )
            }

            composable(Routes.Student.route) {
                val vm: StudentViewModel = viewModel(
                    factory = ViewModelFactory {
                        StudentViewModel(verifyUseCase)
                    }
                )

                CertificateScreen(navController, vm)
            }

            composable(Routes.Verify.route) {
                val vm: StudentViewModel = viewModel(
                    factory = ViewModelFactory {
                        StudentViewModel(verifyUseCase)
                    }
                )

                VerificationScreen(vm)
            }

            composable(Routes.Ledger.route) {
                val vm: LedgerViewModel = viewModel(
                    factory = ViewModelFactory {
                        LedgerViewModel(firestoreService, repository)
                    }
                )

                LedgerScreen(navController, vm)
            }

            composable(
                route = "blockDetail/{index}",
                arguments = listOf(
                    navArgument("index") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val index = backStackEntry.arguments?.getInt("index")
                    ?: return@composable

                val vm: LedgerViewModel = viewModel(
                    factory = ViewModelFactory {
                        LedgerViewModel(firestoreService, repository)
                    }
                )

                BlockDetailScreen(
                    navController = navController,
                    block = vm.blocks.find { it.index == index }
                )
            }
        }
    }
}
