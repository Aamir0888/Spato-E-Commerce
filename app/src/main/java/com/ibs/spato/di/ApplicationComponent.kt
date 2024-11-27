package com.ibs.spato.di

import com.ibs.spato.activities.AddUpdateAddressActivity
import com.ibs.spato.activities.ChangePasswordActivity
import com.ibs.spato.activities.ChooseAddressActivity
import com.ibs.spato.activities.ChoosePaymentMethodActivity
import com.ibs.spato.activities.CompanyPolicyActivity
import com.ibs.spato.activities.DashboardActivity
import com.ibs.spato.activities.EditProfileActivity
import com.ibs.spato.activities.ForgotPasswordActivity
import com.ibs.spato.activities.LoginActivity
import com.ibs.spato.activities.MyAddressesActivity
import com.ibs.spato.activities.MyCartActivity
import com.ibs.spato.activities.NewsletterActivity
import com.ibs.spato.activities.NotificationsActivity
import com.ibs.spato.activities.OrderDetailsActivity
import com.ibs.spato.activities.OtpVerificationActivity
import com.ibs.spato.activities.ProductByCategoryActivity
import com.ibs.spato.activities.ProductDetailsActivity
import com.ibs.spato.activities.ProductRequestActivity
import com.ibs.spato.activities.SettingsActivity
import com.ibs.spato.activities.SignupActivity
import com.ibs.spato.activities.SplashActivity
import com.ibs.spato.activities.TermsConditionsActivity
import com.ibs.spato.activities.TrackOrderActivity
import com.ibs.spato.fragments.CancelledOrdersFragment
import com.ibs.spato.fragments.CartsFragment
import com.ibs.spato.fragments.CompletedOrdersFragment
import com.ibs.spato.fragments.CurrentOrdersFragment
import com.ibs.spato.fragments.HomeFragment
import com.ibs.spato.fragments.ProductsFragment
import com.ibs.spato.fragments.ProfileFragment
import com.ibs.spato.service.LoginService
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class])
interface ApplicationComponent {
    fun injectDashboard(dashboardActivity: DashboardActivity)
    fun injectSignup(signupActivity: SignupActivity)
    fun injectLogin(loginActivity: LoginActivity)
    fun injectForgotPassword(forgotPasswordActivity: ForgotPasswordActivity)
    fun injectChangePassword(changePasswordActivity: ChangePasswordActivity)
    fun injectOtpVerification(otpVerificationActivity: OtpVerificationActivity)
    fun injectMyCart(myCartActivity: MyCartActivity)
    fun injectHomeFragment(homeFragment: HomeFragment)
    fun injectProductsByCategory(productByCategoryActivity: ProductByCategoryActivity)
    fun injectProductDetails(productDetailsActivity: ProductDetailsActivity)
    fun injectProfileFragment(profileFragment: ProfileFragment)
    fun injectCompanyPolicy(companyPolicyActivity: CompanyPolicyActivity)
    fun injectTermsConditions(termsConditionsActivity: TermsConditionsActivity)
    fun injectProductRequest(productRequestActivity: ProductRequestActivity)
    fun injectCurrentOrders(currentOrdersFragment: CurrentOrdersFragment)
    fun injectCompletedOrders(completedOrdersFragment: CompletedOrdersFragment)
    fun injectCancelledOrders(cancelledOrdersFragment: CancelledOrdersFragment)
    fun injectOrderDetails(orderDetailsActivity: OrderDetailsActivity)
    fun injectTrackOrder(trackOrderActivity: TrackOrderActivity)
    fun injectSplashScreen(splashActivity: SplashActivity)
    fun injectEditProfile(editProfileActivity: EditProfileActivity)
    fun injectSettings(settingsActivity: SettingsActivity)
    fun injectMyAddress(myAddressesActivity: MyAddressesActivity)
    fun injectAddUpdateAddress(addUpdateAddressActivity: AddUpdateAddressActivity)
    fun injectProductFragment(productsFragment: ProductsFragment)
    fun injectCartsFragment(cartsFragment: CartsFragment)
    fun injectNotifications(notificationsActivity: NotificationsActivity)
    fun injectNewsletters(newsletterActivity: NewsletterActivity)
    fun injectChooseAddress(chooseAddressActivity: ChooseAddressActivity)
    fun injectChoosePaymentMethod(choosePaymentMethodActivity: ChoosePaymentMethodActivity)
    fun injectLoginService(loginService: LoginService)
}