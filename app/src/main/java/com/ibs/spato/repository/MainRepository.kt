package com.ibs.spato.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ibs.spato.R
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
import com.ibs.spato.retrofit.NoConnectivityException
import com.ibs.spato.retrofit.SpatoAPI
import org.json.JSONObject
import java.lang.Exception
import javax.inject.Inject

class MainRepository @Inject constructor(private val spatoAPI: SpatoAPI, private val context: Context) {
    private val _commonResponse = MutableLiveData<NetworkResult<CommonResponse>>()
    val commonResponse: LiveData<NetworkResult<CommonResponse>> get() = _commonResponse

    private val _registerResponse = MutableLiveData<NetworkResult<RegisterResponse>>()
    val registerResponse: LiveData<NetworkResult<RegisterResponse>> get() = _registerResponse

    private val _loginResponse = MutableLiveData<NetworkResult<Any>>()
    val loginResponse: LiveData<NetworkResult<Any>> get() = _loginResponse

    private val _forgotPasswordResponse = MutableLiveData<NetworkResult<ForgotPasswordResponse>>()
    val forgotPasswordResponse: LiveData<NetworkResult<ForgotPasswordResponse>> get() = _forgotPasswordResponse

    private val _myCartResponse = MutableLiveData<NetworkResult<MyCartResponse>>()
    val myCartResponse: LiveData<NetworkResult<MyCartResponse>> get() = _myCartResponse

    private val _deleteCartItemResponse = MutableLiveData<NetworkResult<DeleteCartItemResponse>>()
    val deleteCartItemResponse: LiveData<NetworkResult<DeleteCartItemResponse>> get() = _deleteCartItemResponse

    private val _topProductListResponse = MutableLiveData<NetworkResult<ProductListResponse>>()
    val topProductListResponse: LiveData<NetworkResult<ProductListResponse>> get() = _topProductListResponse

    private val _productListResponse = MutableLiveData<NetworkResult<ProductListResponse>>()
    val productListResponse: LiveData<NetworkResult<ProductListResponse>> get() = _productListResponse

    private val _categoryListResponse = MutableLiveData<NetworkResult<CategoryListResponse>>()
    val categoryListResponse: LiveData<NetworkResult<CategoryListResponse>> get() = _categoryListResponse

    private val _productsBySearchResponse = MutableLiveData<NetworkResult<ProductListResponse>>()
    val productsBySearchResponse: LiveData<NetworkResult<ProductListResponse>> get() = _productsBySearchResponse

    private val _productDetailsResponse = MutableLiveData<NetworkResult<ProductDetailsResponse>>()
    val productDetailsResponse: LiveData<NetworkResult<ProductDetailsResponse>> get() = _productDetailsResponse

    private val _addToCartResponse = MutableLiveData<NetworkResult<AddToCartResponse>>()
    val addToCartResponse: LiveData<NetworkResult<AddToCartResponse>> get() = _addToCartResponse

    private val _customerDetailsResponse = MutableLiveData<NetworkResult<CustomerDetailsResponse>>()
    val customerDetailsResponse: LiveData<NetworkResult<CustomerDetailsResponse>> get() = _customerDetailsResponse

    private val _policyResponse = MutableLiveData<NetworkResult<PolicyResponse>>()
    val policyResponse: LiveData<NetworkResult<PolicyResponse>> get() = _policyResponse

    private val _myOrderListResponse = MutableLiveData<NetworkResult<MyOrdersResponse>>()
    val myOrderListResponse: LiveData<NetworkResult<MyOrdersResponse>> get() = _myOrderListResponse

    private val _orderDetailsResponse = MutableLiveData<NetworkResult<OrderDetailsResponse>>()
    val orderDetailsResponse: LiveData<NetworkResult<OrderDetailsResponse>> get() = _orderDetailsResponse

    private val _trackOrderResponse = MutableLiveData<NetworkResult<TrackOrderResponse>>()
    val trackOrderResponse: LiveData<NetworkResult<TrackOrderResponse>> get() = _trackOrderResponse

    private val _logoutResponse = MutableLiveData<NetworkResult<Unit>>()
    val logoutResponse: LiveData<NetworkResult<Unit>> get() = _logoutResponse

    private val _editProfileResponse = MutableLiveData<NetworkResult<EditProfileResponse>>()
    val editProfileResponse: LiveData<NetworkResult<EditProfileResponse>> get() = _editProfileResponse

    private val _addressListResponse = MutableLiveData<NetworkResult<AddressListResponse>>()
    val addressListResponse: LiveData<NetworkResult<AddressListResponse>> get() = _addressListResponse

    private val _regionIdResponse = MutableLiveData<NetworkResult<GetRegionIdResponse>>()
    val regionIdResponse: LiveData<NetworkResult<GetRegionIdResponse>> get() = _regionIdResponse

    private val _notificationListResponse = MutableLiveData<NetworkResult<NotificationListResponse>>()
    val notificationListResponse: LiveData<NetworkResult<NotificationListResponse>> get() = _notificationListResponse

    private val _orderPlaceResponse = MutableLiveData<NetworkResult<OrderPlaceResponse>>()
    val orderPlaceResponse: LiveData<NetworkResult<OrderPlaceResponse>> get() = _orderPlaceResponse

    private val _newsletterListResponse = MutableLiveData<NetworkResult<NewsletterListResponse>>()
    val newsletterListResponse: LiveData<NetworkResult<NewsletterListResponse>> get() = _newsletterListResponse


    suspend fun registerCustomer(firstName: String, lastName: String, email: String, mobNumber: String, password: String) {
        try {
            _registerResponse.postValue(NetworkResult.Loading())
            val result = spatoAPI.registerCustomer(email, firstName, lastName, mobNumber, password)
            if (result.isSuccessful && result.body() != null && result.code() == 200) {
                if (result.body()!!.status == 200) {
                    _registerResponse.postValue(NetworkResult.Success(result.body()))
                } else {
                    _registerResponse.postValue(NetworkResult.Error(result.body()!!.message))
                }
            } else {
                _registerResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
            }
        } catch (e: NoConnectivityException) {
            _registerResponse.postValue(NetworkResult.NoInternet(e.message))
        } catch (e_registerResponse: Exception) {
            _registerResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
        }
    }

    suspend fun loginUser(username: String, password: String) {
        try {
            _loginResponse.postValue(NetworkResult.Loading())
            val result = spatoAPI.loginUser(username, password)
            if (result.isSuccessful && result.body() != null && result.code() == 200) {
                _loginResponse.postValue(NetworkResult.Success(result.body()))
            } else if (result.errorBody() != null && result.code() == 401) {
                val errorObj = JSONObject(result.errorBody()!!.charStream().readText())
                val error = errorObj.getString("message")
                _loginResponse.postValue(NetworkResult.Error(error))
            } else {
                _loginResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
            }
        } catch (e: NoConnectivityException) {
            _loginResponse.postValue(NetworkResult.NoInternet(e.message))
        } catch (e_registerResponse: Exception) {
            _loginResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
        }
    }

    suspend fun forgotPassword(email: String) {
        try {
            _forgotPasswordResponse.postValue(NetworkResult.Loading())
            val result = spatoAPI.forgotPassword(email)
            if (result.isSuccessful && result.body() != null && result.code() == 200) {
                if (result.body()!!.status == 200) {
                    _forgotPasswordResponse.postValue(NetworkResult.Success(result.body()))
                } else {
                    _forgotPasswordResponse.postValue(NetworkResult.Error(result.body()!!.message))
                }
            } else {
                _forgotPasswordResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
            }
        } catch (e: NoConnectivityException) {
            _forgotPasswordResponse.postValue(NetworkResult.NoInternet(e.message))
        } catch (e: Exception) {
            _forgotPasswordResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
        }
    }

    suspend fun optVerification(email: String, otp: Int, type: String) {
        try {
            _commonResponse.postValue(NetworkResult.Loading())
            val result = spatoAPI.optVerification(email, otp, type)
            if (result.isSuccessful && result.body() != null && result.code() == 200) {
                if (result.body()!!.status == 200) {
                    _commonResponse.postValue(NetworkResult.Success(result.body()))
                } else {
                    _commonResponse.postValue(NetworkResult.Error(result.body()!!.message))
                }
            } else {
                _commonResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
            }
        } catch (e: NoConnectivityException) {
            _commonResponse.postValue(NetworkResult.NoInternet(e.message))
        } catch (e: Exception) {
            _commonResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
        }
    }

    suspend fun resetPassword(email: String, password: String) {
        try {
            _commonResponse.postValue(NetworkResult.Loading())
            val result = spatoAPI.resetPassword(email, password)
            if (result.isSuccessful && result.body() != null && result.code() == 200) {
                if (result.body()!!.status == 200) {
                    _commonResponse.postValue(NetworkResult.Success(result.body()))
                } else {
                    _commonResponse.postValue(NetworkResult.Error(result.body()!!.message))
                }
            } else {
                _commonResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
            }
        } catch (e: NoConnectivityException) {
            _commonResponse.postValue(NetworkResult.NoInternet(e.message))
        } catch (e: Exception) {
            _commonResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
        }
    }

    suspend fun cartList() {
        try {
            _myCartResponse.postValue(NetworkResult.Loading())
            val result = spatoAPI.cartList()
            if (result.isSuccessful && result.body() != null && result.code() == 200) {
                if (result.body()!!.status == 200) {
                    _myCartResponse.postValue(NetworkResult.Success(result.body()))
                } else {
                    _myCartResponse.postValue(NetworkResult.Error(result.body()!!.message))
                }
            } else {
                _myCartResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
            }
        } catch (e: NoConnectivityException) {
            _myCartResponse.postValue(NetworkResult.NoInternet(e.message))
        } catch (e: Exception) {
            _myCartResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
        }
    }

//    suspend fun cartList2(){
//        safeApiCall(context, _myCartResponse) {spatoAPI.cartList()}
//    }

    suspend fun deleteCartItem(itemId: Long) {
        try {
            _deleteCartItemResponse.postValue(NetworkResult.Loading())
            val result = spatoAPI.deleteCartItem(itemId)
            if (result.isSuccessful && result.body() != null && result.code() == 200) {
                if (result.body()!!.status == 200) {
                    _deleteCartItemResponse.postValue(NetworkResult.Success(result.body()))
                } else {
                    _deleteCartItemResponse.postValue(NetworkResult.Error(result.body()!!.message))
                }
            } else {
                _deleteCartItemResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
            }
        } catch (e: NoConnectivityException) {
            _deleteCartItemResponse.postValue(NetworkResult.NoInternet(e.message))
        } catch (e: Exception) {
            _deleteCartItemResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
        }
    }

    suspend fun updateCartItem(itemId: Long, qty: Int, productId: Long) {
        try {
            _commonResponse.postValue(NetworkResult.Loading())
            val result = spatoAPI.updateCartItem(itemId, qty, productId)
            if (result.isSuccessful && result.body() != null && result.code() == 200) {
                if (result.body()!!.status == 200) {
                    _commonResponse.postValue(NetworkResult.Success(result.body()))
                } else {
                    _commonResponse.postValue(NetworkResult.Error(result.body()!!.message))
                }
            } else {
                _commonResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
            }
        } catch (e: NoConnectivityException) {
            _commonResponse.postValue(NetworkResult.NoInternet(e.message))
        } catch (e: Exception) {
            _commonResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
        }
    }

    suspend fun topProductList() {
        try {
            _topProductListResponse.postValue(NetworkResult.Loading())
            val result = spatoAPI.topProductList()
            if (result.isSuccessful && result.body() != null && result.code() == 200) {
                if (result.body()!!.status == 200) {
                    _topProductListResponse.postValue(NetworkResult.Success(result.body()))
                } else {
                    _topProductListResponse.postValue(NetworkResult.Error(result.body()!!.message))
                }
            } else {
                _topProductListResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
            }
        } catch (e: NoConnectivityException) {
            _topProductListResponse.postValue(NetworkResult.NoInternet(e.message))
        } catch (e: Exception) {
            _topProductListResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
        }
    }

    suspend fun productList(currentPage: Int) {
        try {
            _productListResponse.postValue(NetworkResult.Loading())
            val result = spatoAPI.productList(currentPage)
            if (result.isSuccessful && result.body() != null && result.code() == 200) {
                if (result.body()!!.status == 200) {
                    _productListResponse.postValue(NetworkResult.Success(result.body()))
                } else {
                    _productListResponse.postValue(NetworkResult.Error(result.body()!!.message))
                }
            } else {
                _productListResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
            }
        } catch (e: NoConnectivityException) {
            _productListResponse.postValue(NetworkResult.NoInternet(e.message))
        } catch (e: Exception) {
            _productListResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
        }
    }

    suspend fun productsByCategory(categoryId: Long) {
        try {
            _productListResponse.postValue(NetworkResult.Loading())
            val result = spatoAPI.productsByCategory(categoryId)
            if (result.isSuccessful && result.body() != null && result.code() == 200) {
                if (result.body()!!.status == 200) {
                    _productListResponse.postValue(NetworkResult.Success(result.body()))
                } else {
                    _productListResponse.postValue(NetworkResult.Error(result.body()!!.message))
                }
            } else {
                _productListResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
            }
        } catch (e: NoConnectivityException) {
            _productListResponse.postValue(NetworkResult.NoInternet(e.message))
        } catch (e: Exception) {
            _productListResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
        }
    }

    suspend fun sparePartsList(productId: Long) {
        try {
            _productListResponse.postValue(NetworkResult.Loading())
            val result = spatoAPI.sparePartsList(productId)
            if (result.isSuccessful && result.body() != null && result.code() == 200) {
                if (result.body()!!.status == 200) {
                    _productListResponse.postValue(NetworkResult.Success(result.body()))
                } else {
                    _productListResponse.postValue(NetworkResult.Error(result.body()!!.message))
                }
            } else {
                _productListResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
            }
        } catch (e: NoConnectivityException) {
            _productListResponse.postValue(NetworkResult.NoInternet(e.message))
        } catch (e: Exception) {
            _productListResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
        }
    }

    suspend fun similarProductList(productId: Long) {
        try {
            _productListResponse.postValue(NetworkResult.Loading())
            val result = spatoAPI.similarProductList(productId)
            if (result.isSuccessful && result.body() != null && result.code() == 200) {
                if (result.body()!!.status == 200) {
                    _productListResponse.postValue(NetworkResult.Success(result.body()))
                } else {
                    _productListResponse.postValue(NetworkResult.Error(result.body()!!.message))
                }
            } else {
                _productListResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
            }
        } catch (e: NoConnectivityException) {
            _productListResponse.postValue(NetworkResult.NoInternet(e.message))
        } catch (e: Exception) {
            _productListResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
        }
    }

    suspend fun categoryList() {
        try {
            _categoryListResponse.postValue(NetworkResult.Loading())
            val result = spatoAPI.categoryList()
            if (result.isSuccessful && result.body() != null && result.code() == 200) {
                if (result.body()!!.status == 200) {
                    _categoryListResponse.postValue(NetworkResult.Success(result.body()))
                } else {
                    _categoryListResponse.postValue(NetworkResult.Error(result.body()!!.message))
                }
            } else {
                _categoryListResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
            }
        } catch (e: NoConnectivityException) {
            _categoryListResponse.postValue(NetworkResult.NoInternet(e.message))
        } catch (e: Exception) {
            _categoryListResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
        }
    }

    suspend fun childCategories(categoryId: Long) {
        try {
            _categoryListResponse.postValue(NetworkResult.Loading())
            val result = spatoAPI.childCategories(categoryId)
            if (result.isSuccessful && result.body() != null && result.code() == 200) {
                if (result.body()!!.status == 200) {
                    _categoryListResponse.postValue(NetworkResult.Success(result.body()))
                } else {
                    _categoryListResponse.postValue(NetworkResult.Error(result.body()!!.message))
                }
            } else {
                _categoryListResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
            }
        } catch (e: NoConnectivityException) {
            _categoryListResponse.postValue(NetworkResult.NoInternet(e.message))
        } catch (e: Exception) {
            _categoryListResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
        }
    }

    suspend fun productDetails(productId: Long) {
        try {
            _productDetailsResponse.postValue(NetworkResult.Loading())
            val result = spatoAPI.productDetails(productId)
            if (result.isSuccessful && result.body() != null && result.code() == 200) {
                if (result.body()!!.status == 200) {
                    _productDetailsResponse.postValue(NetworkResult.Success(result.body()))
                } else {
                    _productDetailsResponse.postValue(NetworkResult.Error(result.body()!!.message))
                }
            } else {
                _productDetailsResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
            }
        } catch (e: NoConnectivityException) {
            _productDetailsResponse.postValue(NetworkResult.NoInternet(e.message))
        } catch (e: Exception) {
            _productDetailsResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
        }
    }

    suspend fun addToCart(productId: Long, qty: Int, productType: String) {
        try {
            _addToCartResponse.postValue(NetworkResult.Loading())
            val result = spatoAPI.addToCart(productId, qty, productType)
            if (result.isSuccessful && result.body() != null && result.code() == 200) {
                if (result.body()!!.status == 200) {
                    _addToCartResponse.postValue(NetworkResult.Success(result.body()))
                } else {
                    _addToCartResponse.postValue(NetworkResult.Error(result.body()!!.message))
                }
            } else {
                _addToCartResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
            }
        } catch (e: NoConnectivityException) {
            _addToCartResponse.postValue(NetworkResult.NoInternet(e.message))
        } catch (e: Exception) {
            _addToCartResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
        }
    }

    suspend fun customerDetails() {
        try {
            _customerDetailsResponse.postValue(NetworkResult.Loading())
            val result = spatoAPI.customerDetails()
            if (result.isSuccessful && result.body() != null && result.code() == 200) {
                if (result.body()!!.status == 200) {
                    _customerDetailsResponse.postValue(NetworkResult.Success(result.body()))
                } else {
                    _customerDetailsResponse.postValue(NetworkResult.Error(result.body()!!.message))
                }
            } else {
                _customerDetailsResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
            }
        } catch (e: NoConnectivityException) {
            _customerDetailsResponse.postValue(NetworkResult.NoInternet(e.message))
        } catch (e: Exception) {
            _customerDetailsResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
        }
    }

    suspend fun companyPolicy() {
        try {
            _policyResponse.postValue(NetworkResult.Loading())
            val result = spatoAPI.companyPolicy()
            if (result.isSuccessful && result.body() != null && result.code() == 200) {
                if (result.body()!!.status == 200) {
                    _policyResponse.postValue(NetworkResult.Success(result.body()))
                } else {
                    _policyResponse.postValue(NetworkResult.Error(result.body()!!.message))
                }
            } else {
                _policyResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
            }
        } catch (e: NoConnectivityException) {
            _policyResponse.postValue(NetworkResult.NoInternet(e.message))
        } catch (e: Exception) {
            _policyResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
        }
    }

    suspend fun requestSparePart(requestProductModel: RequestProductModel) {
        try {
            _commonResponse.postValue(NetworkResult.Loading())
            val result = spatoAPI.requestSparePart(requestProductModel)
            if (result.isSuccessful && result.body() != null && result.code() == 200) {
                if (result.body()!!.status == 200) {
                    _commonResponse.postValue(NetworkResult.Success(result.body()))
                } else {
                    _commonResponse.postValue(NetworkResult.Error(result.body()!!.message))
                }
            } else {
                _commonResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
            }
        } catch (e: NoConnectivityException) {
            _commonResponse.postValue(NetworkResult.NoInternet(e.message))
        } catch (e: Exception) {
            _commonResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
        }
    }

    suspend fun orderList(status: String) {
        try {
            _myOrderListResponse.postValue(NetworkResult.Loading())
            val result = spatoAPI.orderList(status)
            if (result.isSuccessful && result.body() != null && result.code() == 200) {
                if (result.body()!!.status == 200) {
                    _myOrderListResponse.postValue(NetworkResult.Success(result.body()))
                } else {
                    _myOrderListResponse.postValue(NetworkResult.Error(result.body()!!.message))
                }
            } else {
                _myOrderListResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
            }
        } catch (e: NoConnectivityException) {
            _myOrderListResponse.postValue(NetworkResult.NoInternet(e.message))
        } catch (e: Exception) {
            _myOrderListResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
        }
    }

    suspend fun orderDetails(orderId: Long) {
        try {
            _orderDetailsResponse.postValue(NetworkResult.Loading())
            val result = spatoAPI.orderDetails(orderId)
            if (result.isSuccessful && result.body() != null && result.code() == 200) {
                if (result.body()!!.status == 200) {
                    _orderDetailsResponse.postValue(NetworkResult.Success(result.body()))
                } else {
                    _orderDetailsResponse.postValue(NetworkResult.Error(result.body()!!.message))
                }
            } else {
                _orderDetailsResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
            }
        } catch (e: NoConnectivityException) {
            _orderDetailsResponse.postValue(NetworkResult.NoInternet(e.message))
        } catch (e: Exception) {
            _orderDetailsResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
        }
    }

    suspend fun trackOrder(orderId: Long) {
        try {
            _trackOrderResponse.postValue(NetworkResult.Loading())
            val result = spatoAPI.trackOrder(orderId)
            if (result.isSuccessful && result.body() != null && result.code() == 200) {
                if (result.body()!!.status == 200) {
                    _trackOrderResponse.postValue(NetworkResult.Success(result.body()))
                } else {
                    _trackOrderResponse.postValue(NetworkResult.Error(result.body()!!.message))
                }
            } else {
                _trackOrderResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
            }
        } catch (e: NoConnectivityException) {
            _trackOrderResponse.postValue(NetworkResult.NoInternet(e.message))
        } catch (e: Exception) {
            _trackOrderResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
        }
    }

    suspend fun logout() {
        try {
            _logoutResponse.postValue(NetworkResult.Loading())
            val result = spatoAPI.logout()
            if (result.isSuccessful && result.code() == 200) {
                _logoutResponse.postValue(NetworkResult.Success())
            } else {
                _logoutResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
            }
        } catch (e: NoConnectivityException) {
            _logoutResponse.postValue(NetworkResult.NoInternet(e.message))
        } catch (e: Exception) {
            _logoutResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
        }
    }

    suspend fun editProfile(
        firstName: String,
        lastName: String,
        mobNumber: String,
        password: String
    ) {
        try {
            _editProfileResponse.postValue(NetworkResult.Loading())
            val result = spatoAPI.editProfile(firstName, lastName, mobNumber, password)
            if (result.isSuccessful && result.body() != null && result.code() == 200) {
                if (result.body()!!.status == 200) {
                    _editProfileResponse.postValue(NetworkResult.Success(result.body()))
                } else {
                    _editProfileResponse.postValue(NetworkResult.Error(result.body()!!.message))
                }
            } else {
                _editProfileResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
            }
        } catch (e: NoConnectivityException) {
            _editProfileResponse.postValue(NetworkResult.NoInternet(e.message))
        } catch (e: Exception) {
            _editProfileResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
        }
    }

    suspend fun editProfilePic(customerProfilePicModel: CustomerProfilePicModel) {
        try {
            _commonResponse.postValue(NetworkResult.Loading())
            val result = spatoAPI.editProfilePic(customerProfilePicModel)
            if (result.isSuccessful && result.body() != null && result.code() == 200) {
                if (result.body()!!.status == 200) {
                    _commonResponse.postValue(NetworkResult.Success(result.body()))
                } else {
                    _commonResponse.postValue(NetworkResult.Error(result.body()!!.message))
                }
            } else {
                _commonResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
            }
        } catch (e: NoConnectivityException) {
            _commonResponse.postValue(NetworkResult.NoInternet(e.message))
        } catch (e: Exception) {
            _commonResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
        }
    }

    suspend fun myAddressList() {
        try {
            _addressListResponse.postValue(NetworkResult.Loading())
            val result = spatoAPI.myAddressList()
            if (result.isSuccessful && result.body() != null && result.code() == 200) {
                if (result.body()!!.status == 200) {
                    _addressListResponse.postValue(NetworkResult.Success(result.body()))
                } else {
                    _addressListResponse.postValue(NetworkResult.Error(result.body()!!.message))
                }
            } else {
                _addressListResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
            }
        } catch (e: NoConnectivityException) {
            _addressListResponse.postValue(NetworkResult.NoInternet(e.message))
        } catch (e: Exception) {
            _addressListResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
        }
    }

    suspend fun deleteAddress(addressId: Long) {
        try {
            _commonResponse.postValue(NetworkResult.Loading())
            val result = spatoAPI.deleteAddress(addressId)
            if (result.isSuccessful && result.body() != null && result.code() == 200) {
                if (result.body()!!.status == 200) {
                    _commonResponse.postValue(NetworkResult.Success(result.body()))
                } else {
                    _commonResponse.postValue(NetworkResult.Error(result.body()!!.message))
                }
            } else {
                _commonResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
            }
        } catch (e: NoConnectivityException) {
            _commonResponse.postValue(NetworkResult.NoInternet(e.message))
        } catch (e: Exception) {
            _commonResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
        }
    }

    suspend fun getRegionId(countryCode: String) {
        try {
            _regionIdResponse.postValue(NetworkResult.Loading())
            val result = spatoAPI.getRegionId(countryCode)
            if (result.isSuccessful && result.body() != null && result.code() == 200) {
                if (result.body()!!.status == 200) {
                    _regionIdResponse.postValue(NetworkResult.Success(result.body()))
                } else {
                    _regionIdResponse.postValue(NetworkResult.Error(result.body()!!.message))
                }
            } else {
                _regionIdResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
            }
        } catch (e: NoConnectivityException) {
            _regionIdResponse.postValue(NetworkResult.NoInternet(e.message))
        } catch (e: Exception) {
            _regionIdResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
        }
    }

    suspend fun addAddress(
        firstName: String,
        lastName: String,
        address: String,
        city: String,
        state: String,
        postCode: Long,
        country: String,
        phoneNumber: String,
        regionId: Long,
        addressId: Long?
    ) {
        try {
            _commonResponse.postValue(NetworkResult.Loading())
            val result = spatoAPI.addAddress(
                addressId,
                postCode,
                firstName,
                lastName,
                address,
                city,
                state,
                country,
                phoneNumber,
                regionId
            )
            if (result.isSuccessful && result.body() != null && result.code() == 200) {
                if (result.body()!!.status == 200) {
                    _commonResponse.postValue(NetworkResult.Success(result.body()))
                } else {
                    _commonResponse.postValue(NetworkResult.Error(result.body()!!.message))
                }
            } else {
                _commonResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
            }
        } catch (e: NoConnectivityException) {
            _commonResponse.postValue(NetworkResult.NoInternet(e.message))
        } catch (e: Exception) {
            _commonResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
        }
    }

    suspend fun searchProduct(query: String) {
        try {
            _productsBySearchResponse.postValue(NetworkResult.Loading())
            val result = spatoAPI.searchProduct(query)
            if (result.isSuccessful && result.body() != null && result.code() == 200) {
                if (result.body()!!.status == 200) {
                    _productsBySearchResponse.postValue(NetworkResult.Success(result.body()))
                } else {
                    _productsBySearchResponse.postValue(NetworkResult.Error(result.body()!!.message))
                }
            } else {
                _productsBySearchResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
            }
        } catch (e: NoConnectivityException) {
            _productsBySearchResponse.postValue(NetworkResult.NoInternet(e.message))
        } catch (e: Exception) {
            _productsBySearchResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
        }
    }

    suspend fun deleteAccount() {
        try {
            _commonResponse.postValue(NetworkResult.Loading())
            val result = spatoAPI.deleteAccount()
            if (result.isSuccessful && result.body() != null && result.code() == 200) {
                if (result.body()!!.status == 200) {
                    _commonResponse.postValue(NetworkResult.Success(result.body()))
                } else {
                    _commonResponse.postValue(NetworkResult.Error(result.body()!!.message))
                }
            } else {
                _commonResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
            }
        } catch (e: NoConnectivityException) {
            _commonResponse.postValue(NetworkResult.NoInternet(e.message))
        } catch (e: Exception) {
            _commonResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
        }
    }

    suspend fun saveFcmToken(fcmDeviceToken: String) {
        try {
            _commonResponse.postValue(NetworkResult.Loading())
            val result = spatoAPI.saveFcmToken(fcmDeviceToken)
            if (result.isSuccessful && result.body() != null && result.code() == 200) {
                if (result.body()!!.status == 200) {
                    _commonResponse.postValue(NetworkResult.Success(result.body()))
                } else {
                    _commonResponse.postValue(NetworkResult.Error(result.body()!!.message))
                }
            } else {
                _commonResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
            }
        } catch (e: NoConnectivityException) {
            _commonResponse.postValue(NetworkResult.NoInternet(e.message))
        } catch (e: Exception) {
            _commonResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
        }
    }

    suspend fun notificationList() {
        try {
            _notificationListResponse.postValue(NetworkResult.Loading())
            val result = spatoAPI.notificationList()
            if (result.isSuccessful && result.body() != null && result.code() == 200) {
                if (result.body()!!.status == 200) {
                    _notificationListResponse.postValue(NetworkResult.Success(result.body()))
                } else {
                    _notificationListResponse.postValue(NetworkResult.Error(result.body()!!.message))
                }
            } else {
                _notificationListResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
            }
        } catch (e: NoConnectivityException) {
            _notificationListResponse.postValue(NetworkResult.NoInternet(e.message))
        } catch (e: Exception) {
            _notificationListResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
        }
    }

    suspend fun deleteNotification(entityId: Long) {
        try {
            _commonResponse.postValue(NetworkResult.Loading())
            val result = spatoAPI.deleteNotification(entityId)
            if (result.isSuccessful && result.body() != null && result.code() == 200) {
                if (result.body()!!.status == 200) {
                    _commonResponse.postValue(NetworkResult.Success(result.body()))
                } else {
                    _commonResponse.postValue(NetworkResult.Error(result.body()!!.message))
                }
            } else {
                _commonResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
            }
        } catch (e: NoConnectivityException) {
            _commonResponse.postValue(NetworkResult.NoInternet(e.message))
        } catch (e: Exception) {
            _commonResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
        }
    }

    suspend fun orderPlace(addressId: Long, cartId: Long, paymentMethod: String) {
        try {
            _orderPlaceResponse.postValue(NetworkResult.Loading())
            val result = spatoAPI.orderPlace(addressId, cartId, paymentMethod)
            if (result.isSuccessful && result.body() != null && result.code() == 200) {
                if (result.body()!!.status == 200) {
                    _orderPlaceResponse.postValue(NetworkResult.Success(result.body()))
                } else {
                    _orderPlaceResponse.postValue(NetworkResult.Error(result.body()!!.message))
                }
            } else {
                _orderPlaceResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
            }
        } catch (e: NoConnectivityException) {
            _orderPlaceResponse.postValue(NetworkResult.NoInternet(e.message))
        } catch (e: Exception) {
            _orderPlaceResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
        }
    }

    suspend fun newsletters() {
        try {
            _newsletterListResponse.postValue(NetworkResult.Loading())
            val result = spatoAPI.newsletters()
            if (result.isSuccessful && result.body() != null && result.code() == 200) {
                if (result.body()!!.status == 200) {
                    _newsletterListResponse.postValue(NetworkResult.Success(result.body()))
                } else {
                    _newsletterListResponse.postValue(NetworkResult.Error(result.body()!!.message))
                }
            } else {
                _newsletterListResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
            }
        } catch (e: NoConnectivityException) {
            _newsletterListResponse.postValue(NetworkResult.NoInternet(e.message))
        } catch (e: Exception) {
            _newsletterListResponse.postValue(NetworkResult.Error(context.getString(R.string.something_went_wrong)))
        }
    }
}