package com.ibs.spato.retrofit

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
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SpatoAPI {

    @GET("mobapi/api/Createcustomer")
    suspend fun registerCustomer(
        @Query("email") email: String,
        @Query("first_name") first_name: String,
        @Query("last_name") last_name: String,
        @Query("mobile_no") mobile_no: String,
        @Query("password") password: String
    ) : Response<RegisterResponse>

    @POST("rest/V1/integration/customer/token")
    suspend fun loginUser(
        @Query("username") username: String,
        @Query("password") password: String) : Response<String>

    @GET("mobapi/api/Forgetpassword")
    suspend fun forgotPassword(
        @Query("email") email: String
    ) : Response<ForgotPasswordResponse>

    @GET("mobapi/api/Verifyotp")
    suspend fun optVerification(
        @Query("email") email: String,
        @Query("otp") otp: Int,
        @Query("type") type: String
    ) : Response<CommonResponse>

    @GET("mobapi/api/Resetpassword")
    suspend fun resetPassword(
        @Query("email") email: String,
        @Query("password") password: String) : Response<CommonResponse>

    @GET("mobapi/api/cartlist")
    suspend fun cartList() : Response<MyCartResponse>

    @GET("mobapi/api/Removecartitem")
    suspend fun deleteCartItem(@Query("item_id") item_id: Long) : Response<DeleteCartItemResponse>

    @GET("mobapi/api/Updatecartitems")
    suspend fun updateCartItem(
        @Query("item_id") item_id: Long,
        @Query("qty") qty: Int,
        @Query("product_id") product_id: Long) : Response<CommonResponse>

    @GET("mobapi/api/Productlisttop")
    suspend fun topProductList() : Response<ProductListResponse>

    @GET("mobapi/api/Productlist")
    suspend fun productList(@Query("currentpage") currentpage: Int) : Response<ProductListResponse>

    @GET("mobapi/api/Productlist")
    suspend fun productsByCategory(@Query("category_id") category_id: Long) : Response<ProductListResponse>

    @GET("mobapi/api/Productpartslist")
    suspend fun sparePartsList(@Query("product_id") product_id: Long) : Response<ProductListResponse>

    @GET("mobapi/api/Similarproducts")
    suspend fun similarProductList(@Query("product_id") product_id: Long) : Response<ProductListResponse>

    @GET("mobapi/api/Categorylist")
    suspend fun categoryList() : Response<CategoryListResponse>

    @GET("mobapi/api/Categorylist")
    suspend fun childCategories(@Query("category_id") category_id: Long) : Response<CategoryListResponse>

    @GET("mobapi/api/Productdetail")
    suspend fun productDetails(@Query("product_id") product_id: Long) : Response<ProductDetailsResponse>

    @GET("mobapi/api/Addtocart")
    suspend fun addToCart(@Query("product_id") product_id: Long,
                          @Query("qty") qty: Int,
                          @Query("productType") productType: String) : Response<AddToCartResponse>
    @GET("mobapi/api/Customerdetails")
    suspend fun customerDetails() : Response<CustomerDetailsResponse>

    @GET("mobapi/api/Contentpage")
    suspend fun companyPolicy() : Response<PolicyResponse>

    @POST("mobapi/api/Productquote")
    suspend fun requestSparePart(@Body requestProductModel: RequestProductModel) : Response<CommonResponse>

    @GET("mobapi/api/Orderlist")
    suspend fun orderList(@Query("status") status: String) : Response<MyOrdersResponse>

    @GET("mobapi/api/Orderdetail")
    suspend fun orderDetails(@Query("order_id") order_id: Long) : Response<OrderDetailsResponse>

    @GET("mobapi/api/Trackorder")
    suspend fun trackOrder(@Query("order_id") order_id: Long) : Response<TrackOrderResponse>

    @GET("mobapi/api/Logout")
    suspend fun logout() : Response<Unit>

    @GET("mobapi/api/Customerupdateaccount")
    suspend fun editProfile(@Query("first_name") first_name: String,
                            @Query("last_name") last_name: String,
                            @Query("mobile_no") mobile_no: String,
                            @Query("password") password: String) : Response<EditProfileResponse>

    @POST("mobapi/api/Customerphoto")
    suspend fun editProfilePic(@Body customerProfilePicModel: CustomerProfilePicModel) : Response<CommonResponse>

    @GET("mobapi/api/Customeraddresslist")
    suspend fun myAddressList() : Response<AddressListResponse>

    @GET("mobapi/api/Customerdeleteaddress")
    suspend fun deleteAddress(@Query("address_id") address_id: Long) : Response<CommonResponse>

    @GET("mobapi/api/Regions")
    suspend fun getRegionId(@Query("country_code") country_code: String) : Response<GetRegionIdResponse>

    @GET("mobapi/api/Customeraddaddress")
    suspend fun addAddress(@Query("address_id") address_id: Long?,
                           @Query("postcode") postcode: Long,
                           @Query("first_name") first_name: String,
                           @Query("last_name") last_name: String,
                           @Query("address") address: String,
                           @Query("city") city: String,
                           @Query("state") state: String,
                           @Query("country") country: String,
                           @Query("phoneNo") phoneNo: String,
                           @Query("regionId") regionId: Long) : Response<CommonResponse>

    @GET("mobapi/api/Productsearch")
    suspend fun searchProduct(@Query("query") query: String) : Response<ProductListResponse>

    @GET("mobapi/api/Customerdelete")
    suspend fun deleteAccount() : Response<CommonResponse>

    @GET("mobapi/api/Savedevicetoken")
    suspend fun saveFcmToken(@Query("device_token") device_token: String) : Response<CommonResponse>

    @GET("mobapi/api/Notificationlist")
    suspend fun notificationList() : Response<NotificationListResponse>

    @GET("mobapi/api/Notificationdelete")
    suspend fun deleteNotification(@Query("entity_id") entity_id: Long) : Response<CommonResponse>

    @GET("mobapi/api/Orderplace")
    suspend fun orderPlace(@Query("address_id") address_id: Long,
                           @Query("cart_id") cart_id: Long,
                           @Query("payment_method") payment_method: String) : Response<OrderPlaceResponse>

    @GET("mobapi/api/Newsletterlist")
    suspend fun newsletters() : Response<NewsletterListResponse>
}