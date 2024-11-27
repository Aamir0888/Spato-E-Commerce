package com.ibs.spato.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ibs.spato.repository.MainRepository
import com.ibs.spato.responses.add_to_cart.AddToCartResponse
import com.ibs.spato.responses.my_addresses.address_list.AddressListResponse
import com.ibs.spato.responses.common_response.CommonResponse
import com.ibs.spato.responses.forgot_password.ForgotPasswordResponse
import com.ibs.spato.responses.cart_list.MyCartResponse
import com.ibs.spato.responses.customer_details.CustomerDetailsResponse
import com.ibs.spato.responses.dashboard_category_list.CategoryListResponse
import com.ibs.spato.responses.dashboard_product_list.ProductListResponse
import com.ibs.spato.responses.delete_cart_item.DeleteCartItemResponse
import com.ibs.spato.responses.edit_profile.EditProfileResponse
import com.ibs.spato.responses.models.customer_profile_pic.CustomerProfilePicModel
import com.ibs.spato.responses.models.request_product.RequestProductModel
import com.ibs.spato.responses.my_addresses.get_region_id.GetRegionIdResponse
import com.ibs.spato.responses.my_orders_responses.all_orders.MyOrdersResponse
import com.ibs.spato.responses.my_orders_responses.order_details.OrderDetailsResponse
import com.ibs.spato.responses.my_orders_responses.track_order.TrackOrderResponse
import com.ibs.spato.responses.newsletter_list.NewsletterListResponse
import com.ibs.spato.responses.notification_list.NotificationListResponse
import com.ibs.spato.responses.order_place.OrderPlaceResponse
import com.ibs.spato.responses.privacy_policy_terms_conditions.PolicyResponse
import com.ibs.spato.responses.product_details.ProductDetailsResponse
import com.ibs.spato.responses.register.RegisterResponse
import com.ibs.spato.retrofit.NetworkResult
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(private val mainRepository: MainRepository) : ViewModel() {
    val registerResponse: LiveData<NetworkResult<RegisterResponse>> get() = mainRepository.registerResponse
    val loginResponse: LiveData<NetworkResult<Any>> get() = mainRepository.loginResponse
    val forgotPasswordResponse: LiveData<NetworkResult<ForgotPasswordResponse>> get() = mainRepository.forgotPasswordResponse
    val commonResponse: LiveData<NetworkResult<CommonResponse>> get() = mainRepository.commonResponse
    val cartListResponse: LiveData<NetworkResult<MyCartResponse>> get() = mainRepository.myCartResponse
    val deleteCartItemResponse: LiveData<NetworkResult<DeleteCartItemResponse>> get() = mainRepository.deleteCartItemResponse
    val topProductListResponse: LiveData<NetworkResult<ProductListResponse>> get() = mainRepository.topProductListResponse
    val productListResponse: LiveData<NetworkResult<ProductListResponse>> get() = mainRepository.productListResponse
    val productsBySearchResponse: LiveData<NetworkResult<ProductListResponse>> get() = mainRepository.productsBySearchResponse
    val categoryListResponse: LiveData<NetworkResult<CategoryListResponse>> get() = mainRepository.categoryListResponse
    val productDetailsResponse: LiveData<NetworkResult<ProductDetailsResponse>> get() = mainRepository.productDetailsResponse
    val addToCartResponse: LiveData<NetworkResult<AddToCartResponse>> get() = mainRepository.addToCartResponse
    val customerDetailsResponse: LiveData<NetworkResult<CustomerDetailsResponse>> get() = mainRepository.customerDetailsResponse
    val policyResponse: LiveData<NetworkResult<PolicyResponse>> get() = mainRepository.policyResponse
    val myOrderListResponse: LiveData<NetworkResult<MyOrdersResponse>> get() = mainRepository.myOrderListResponse
    val orderDetailsResponse: LiveData<NetworkResult<OrderDetailsResponse>> get() = mainRepository.orderDetailsResponse
    val trackOrderResponse: LiveData<NetworkResult<TrackOrderResponse>> get() = mainRepository.trackOrderResponse
    val logoutResponse: LiveData<NetworkResult<Unit>> get() = mainRepository.logoutResponse
    val editProfileResponse: LiveData<NetworkResult<EditProfileResponse>> get() = mainRepository.editProfileResponse
    val addressListResponse: LiveData<NetworkResult<AddressListResponse>> get() = mainRepository.addressListResponse
    val regionIdResponse: LiveData<NetworkResult<GetRegionIdResponse>> get() = mainRepository.regionIdResponse
    val notificationListResponse: LiveData<NetworkResult<NotificationListResponse>> get() = mainRepository.notificationListResponse
    val orderPlaceResponse: LiveData<NetworkResult<OrderPlaceResponse>> get() = mainRepository.orderPlaceResponse
    val newsletterListResponse: LiveData<NetworkResult<NewsletterListResponse>> get() = mainRepository.newsletterListResponse

    fun registerCustomer(firstName: String, lastName: String, email: String, mobNumber: String, password: String) {
        viewModelScope.launch {
            mainRepository.registerCustomer(firstName, lastName, email, mobNumber, password)
        }
    }

    fun loginUser(username: String, password: String) {
        viewModelScope.launch {
            mainRepository.loginUser(username, password)
        }
    }

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            mainRepository.forgotPassword(email)
        }
    }

    fun otpVerification(email: String, otp: Int, type: String) {
        viewModelScope.launch {
            mainRepository.optVerification(email, otp, type)
        }
    }

    fun resetPassword(email: String, password: String) {
        viewModelScope.launch {
            mainRepository.resetPassword(email, password)
        }
    }

    fun cartList() {
        viewModelScope.launch {
            mainRepository.cartList()
        }
    }

    fun deleteCartItem(itemId: Long) {
        viewModelScope.launch {
            mainRepository.deleteCartItem(itemId)
        }
    }

    fun updateCartItem(itemId: Long, qty: Int, productId: Long) {
        viewModelScope.launch {
            mainRepository.updateCartItem(itemId, qty, productId)
        }
    }

    fun topProductList() {
        viewModelScope.launch {
            mainRepository.topProductList()
        }
    }

    fun categoryList() {
        viewModelScope.launch {
            mainRepository.categoryList()
        }
    }

    fun childCategories(categoryId: Long) {
        viewModelScope.launch {
            mainRepository.childCategories(categoryId)
        }
    }

    fun productList(currentPage: Int) {
        viewModelScope.launch {
            mainRepository.productList(currentPage)
        }
    }

    fun productsByCategory(categoryId: Long) {
        viewModelScope.launch {
            mainRepository.productsByCategory(categoryId)
        }
    }

    fun sparePartsList(productId: Long) {
        viewModelScope.launch {
            mainRepository.sparePartsList(productId)
        }
    }

    fun similarProductList(productId: Long) {
        viewModelScope.launch {
            mainRepository.similarProductList(productId)
        }
    }

    fun productDetails(productId: Long) {
        viewModelScope.launch {
            mainRepository.productDetails(productId)
        }
    }

    fun addToCart(productId: Long, qty: Int, productType: String) {
        viewModelScope.launch {
            mainRepository.addToCart(productId, qty, productType)
        }
    }

    fun customerDetails() {
        viewModelScope.launch {
            mainRepository.customerDetails()
        }
    }

    fun companyPolicy() {
        viewModelScope.launch {
            mainRepository.companyPolicy()
        }
    }

    fun requestSparePart(requestProductModel: RequestProductModel) {
        viewModelScope.launch {
            mainRepository.requestSparePart(requestProductModel)
        }
    }

    fun orderList(status: String) {
        viewModelScope.launch {
            mainRepository.orderList(status)
        }
    }

    fun orderDetails(orderId: Long) {
        viewModelScope.launch {
            mainRepository.orderDetails(orderId)
        }
    }

    fun trackOrder(orderId: Long) {
        viewModelScope.launch {
            mainRepository.trackOrder(orderId)
        }
    }

    fun logout() {
        viewModelScope.launch {
            mainRepository.logout()
        }
    }

    fun editProfile(firstName: String, lastName: String, mobNumber: String, password: String) {
        viewModelScope.launch {
            mainRepository.editProfile(firstName, lastName, mobNumber, password)
        }
    }

    fun editProfilePic(customerProfilePicModel: CustomerProfilePicModel) {
        viewModelScope.launch {
            mainRepository.editProfilePic(customerProfilePicModel)
        }
    }

    fun myAddressList() {
        viewModelScope.launch {
            mainRepository.myAddressList()
        }
    }

    fun deleteAddress(addressId: Long) {
        viewModelScope.launch {
            mainRepository.deleteAddress(addressId)
        }
    }

    fun getRegionId(countryCode: String) {
        viewModelScope.launch {
            mainRepository.getRegionId(countryCode)
        }
    }

    fun addAddress(firstName: String, lastName: String, address: String, city: String, state: String, postCode: Long, country: String, phoneNumber: String, regionId: Long, addressId: Long?) {
        viewModelScope.launch {
            mainRepository.addAddress(firstName, lastName, address, city, state, postCode, country, phoneNumber, regionId, addressId)
        }
    }

    fun searchProduct(query: String) {
        viewModelScope.launch {
            mainRepository.searchProduct(query)
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            mainRepository.deleteAccount()
        }
    }

    fun saveFcmToken(fcmDeviceToken: String) {
        viewModelScope.launch {
            mainRepository.saveFcmToken(fcmDeviceToken)
        }
    }

    fun notificationList() {
        viewModelScope.launch {
            mainRepository.notificationList()
        }
    }

    fun deleteNotification(entityId: Long) {
        viewModelScope.launch {
            mainRepository.deleteNotification(entityId)
        }
    }

    fun orderPlace(addressId: Long, cartId: Long, paymentMethod: String) {
        viewModelScope.launch {
            mainRepository.orderPlace(addressId, cartId, paymentMethod)
        }
    }

    fun newsletters() {
        viewModelScope.launch {
            mainRepository.newsletters()
        }
    }
}