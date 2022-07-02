//
// Created by Виталий Хлудеев on 07.05.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import Alamofire
import SwiftyJSON
import UIKit

extension Dictionary {
    mutating func update(other:Dictionary) {
        for (key,value) in other {
            self.updateValue(value, forKey:key)
        }
    }
}

class ApiRequester {

//    public static let domain = "http://oskelly.tk"
   public static let domain = "https://oskelly.ru"
//   public static let domain = "http://localhost:8080"
//    static let bankEndpoint = "https://mdm-webapi-mdmpay-financial-staging.mdmbank.ru"
    static let bankEndpoint = "https://pay.mdm.ru"

   func authenticate(email: String, password: String) -> DataRequest {

       let parameters: Parameters = [
               "email": email,
               "password": password,
       ]

       return Alamofire.request(ApiRequester.domain + "/mobile/api/v1/users/authentication", method: .post, parameters: parameters)
   }

    func register(fullName: String, email: String, password: String, confirmPassword: String, phone: String) -> DataRequest {
        let parameters: Parameters = [
                "email": email,
                "password": password,
                "confirmPassword": confirmPassword,
                "nickname": fullName,
                "phone": phone
        ]
        return Alamofire.request(ApiRequester.domain + "/mobile/api/v1/users/registration", method: .post, parameters: parameters)
    }

    func saveDeviceToken(token: String) {
        let parameters: Parameters = [
            "token": token
        ]
        self.sendAuthorizedRequest(endpoint: "/mobile/api/v1/users/device/apple", method: .post, parameters: parameters) { responseObject, error in

        }
    }

    func search(search : String, completionHandler: @escaping (ProductCollectionPage) -> ()) {
        let parameters: Parameters = [
            "search": search
        ]
        self.sendAuthorizedRequest(endpoint: "/mobile/api/v1/catalog/search", method: .get, parameters: parameters) { responseObject, error in
            self.handleProductList(responseObject: responseObject, completionHandler: completionHandler)
        }
    }

    func getNewArrivals(completionHandler: @escaping (ProductCollectionPage) -> ()) {

        let formatter = NumberFormatter()
        formatter.generatesDecimalNumbers = true
        formatter.numberStyle = NumberFormatter.Style.decimal

        self.sendAuthorizedRequest(endpoint: "/mobile/api/v1/catalog/new2", method: .get, parameters: [:], completionHandler: { responseObject, error in
            self.handleProductList(responseObject: responseObject, completionHandler: completionHandler)
        }, errorHandler: { e in

        }, needAuthorize: false)
    }

    func getProducts(productRequest: ProductRequest, completionHandler: @escaping (ProductCollectionPage) -> ()) {
        self.sendAuthorizedRequest(endpoint: "/mobile/api/v1/catalog/products", method: .get, parameters: productRequest.dictionaryRepresentation()) { responseObject, error in
            self.handleProductList(responseObject: responseObject, completionHandler: completionHandler)
        }
    }

    func getWishList(page: Int, profileId: Int, completionHandler: @escaping (ProductCollectionPage) -> ()) {
        let parameters: Parameters = [
            "page": page
        ]

        self.sendAuthorizedRequest(endpoint: "/mobile/api/v1/catalog/wishlist/" + String(profileId), method: .get, parameters: parameters) { responseObject, error in
            self.handleProductList(responseObject: responseObject, completionHandler: completionHandler)
        }
    }

    func getPriceFollowings(completionHandler: @escaping (ProductCollectionPage) -> ()) {
        self.sendAuthorizedRequest(endpoint: "/mobile/api/v1/catalog/pricefollowings", method: .get, parameters: [:]) { responseObject, error in
            self.handleProductList(responseObject: responseObject, completionHandler: completionHandler)
        }
    }

    func getFavourites(page: Int, profileId: Int, completionHandler: @escaping (ProductCollectionPage) -> ()) {
        let parameters: Parameters = [
            "page": page
        ]

        self.sendAuthorizedRequest(endpoint: "/mobile/api/v1/catalog/favorites/" + String(profileId), method: .get, parameters: parameters) { responseObject, error in
            self.handleProductList(responseObject: responseObject, completionHandler: completionHandler)
        }
    }

    func getCurrentProfile(completionHandler: @escaping (PublicProfile) -> ()) {
        self.sendAuthorizedRequest(endpoint: "/mobile/api/v1/profile", method: .get, parameters: [:]) { responseObject, error in
            completionHandler(PublicProfile(object: responseObject))
        }
    }

    func getAccount(completionHandler: @escaping (Account) -> ()) {
        self.sendAuthorizedRequest(endpoint: "/mobile/api/v1/account", method: .get, parameters: [:]) { responseObject, error in
            completionHandler(Account(object: responseObject))
        }
    }

    func getProfile(profileId: Int, completionHandler: @escaping (PublicProfile) -> ()) {
        self.sendAuthorizedRequest(endpoint: "/mobile/api/v1/profile/" + String(profileId), method: .get, parameters: [:]) { responseObject, error in
            completionHandler(PublicProfile(object: responseObject))
        }
    }

    private func handleProductList(responseObject: Any?, completionHandler: (ProductCollectionPage) -> ()) {
        let page = ProductCollectionPage(object: responseObject)
        if(page != nil && page.items != nil && page.totalPagesCount != nil) {
            completionHandler(page)
        }
    }

    func getDrafts(completionHandler: @escaping ([Draft]) -> ()) {
        self.sendAuthorizedRequest(endpoint: "/mobile/api/v1/publication/drafts", method: .get, parameters: [:]) { responseObject, error in
            var drafts: [Draft] = []
            let json = JSON(responseObject)
            for (index,subJson):(String, JSON) in json {
                let draft = Draft(json: subJson)
                drafts.append(draft)
            }
            completionHandler(drafts)
        }
    }

    func firstStep(categoryId: Int, brandId: Int, completionHandler: @escaping (Draft) -> ()) {
        var parameters: Parameters = [
            "brandId": brandId,
            "categoryId": categoryId
        ]
        self.sendAuthorizedRequest(endpoint: "/mobile/api/v1/publication/step/1", method: .post, parameters: parameters) { responseObject, error in
            completionHandler(Draft(json: JSON(responseObject)))
        }
    }

    func getSaleRequisite(saleId: Int, completionHandler: @escaping (SellerRequisite) -> ()) {
        self.sendAuthorizedRequest(endpoint: "/mobile/api/v1/account/sales/address/" + String(saleId), method: .get, parameters: [:]) { responseObject, error in
            completionHandler(SellerRequisite(json: JSON(responseObject)))
        }
    }

    func getBrands(completionHandler: @escaping ([String:[Brand]]) -> ()) {
        self.sendAuthorizedRequest(endpoint: "/mobile/api/v1/catalog/groupedBrands", method: .get, parameters: [:]) { responseObject, error in
            var groups: [String:[Brand]] = [:]
            let json = JSON(responseObject)
            for (index, brandsJson):(String, JSON) in json {
                var brands: [Brand] = []
                for (index, oneBrandJson):(String, JSON) in brandsJson {
                    brands.append(Brand(id: oneBrandJson["id"].intValue, name: oneBrandJson["name"].stringValue))
                }
                let group = [
                    index: brands
                ]
                groups.update(other: group)
            }
            completionHandler(groups)
        }
    }

    func wishListToggle(productId: Int) {
        self.sendAuthorizedRequest(endpoint: "/mobile/api/v1/wishlist/" + String(productId), method: .post, parameters: [:]) { responseObject, error in

        }
    }

    func priceFollowingToggle(productId: Int) {
        self.sendAuthorizedRequest(endpoint: "/mobile/api/v1/product/" + String(productId) + "/priceFollowing/toggle", method: .post, parameters: [:]) { responseObject, error in

        }
    }

    func likeToggle(productId: Int) {
        self.sendAuthorizedRequest(endpoint: "/mobile/api/v1/product/" + String(productId) + "/like/toggle", method: .post, parameters: [:]) { responseObject, error in

        }
    }

    func followingToggle(profileId: Int, completionHandler: @escaping () -> ()) {
        let params = [
            "userId" : profileId
        ]
        self.sendAuthorizedRequest(endpoint: "/mobile/api/v1/profile/following/toggle", method: .post, parameters: params) { responseObject, error in
            completionHandler()
        }
    }

    func getConditions(completionHandler: @escaping ([ProductCondition]) -> ()) {
        self.sendAuthorizedRequest(endpoint: "/mobile/api/v1/catalog/conditions", method: .get, parameters: [:]) { responseObject, error in
            var list:[ProductCondition] = []
            let json = JSON(responseObject)
            for (index, j):(String, JSON) in json {
                let element = ProductCondition(id: j["id"].intValue, name: j["name"].stringValue, description: j["description"].stringValue)
                list.append(element)
            }
            completionHandler(list)
        }
    }

    func getCategories(_ appendSelf: Bool, completionHandler: @escaping ([ProductCategory]) -> ()) {
        self.sendAuthorizedRequest(endpoint: "/mobile/api/v1/catalog", method: .get, parameters: [:]) { responseObject, error in
            var categories:[ProductCategory] = []
            let json = JSON(responseObject)
            for (index, j):(String, JSON) in json {
                let category = ProductCategory(id: j["id"].intValue, name: j["displayName"].stringValue, children: [], displayInTreeName: j["displayName"].stringValue)
                if let hasChildren = j["hasChildren"].bool {
                    self.addChildren(j["children"], category, appendSelf);
                }
                categories.append(category)
            }
            completionHandler(categories)
        }
    }

    func getAttributes(categoryId: Int, completionHandler: @escaping ([Attribute]) -> ()) {
        self.sendAuthorizedRequest(endpoint: "/mobile/api/v1/catalog/attributes/" + String(categoryId) , method: .get, parameters: [:]) { responseObject, error in
            var attributes:[Attribute] = []
            let json = JSON(responseObject)
            for (index, j):(String, JSON) in json {
                let attribute = Attribute(id: j["id"].intValue, name: j["name"].stringValue, values: [], selectedValue: nil)
                for (index, jj):(String, JSON) in j["values"] {
                    let value = AttributeValue(id: jj["id"].intValue, value: jj["value"].stringValue, attributeId: attribute.id)
                    attribute.values.append(value)
                }
                attributes.append(attribute)
            }
            completionHandler(attributes)
        }
    }

    func getMySaleGroups(completionHandler: @escaping ([SaleGroup]) -> ()) {
        self.sendAuthorizedRequest(endpoint: "/mobile/api/v1/account/sales", method: .get, parameters: [:]) { responseObject, error in
            var groups : [SaleGroup] = []
            let json = JSON(responseObject)
            for (index,subJson):(String, JSON) in json {
                let group = SaleGroup(json: subJson)
                groups.append(group)
            }
            completionHandler(groups)
        }
    }

    func getSale(productItemId: Int, completionHandler: @escaping (Sale) -> (), errorHandler: @escaping (String) -> ()) {
        self.sendAuthorizedRequest(endpoint: "/mobile/api/v1/account/sales/" + String(productItemId), method: .get, parameters: [:], completionHandler: { responseObject, error in
            completionHandler(Sale(object: responseObject))
        }, errorHandler: { e in
            errorHandler(e)
        })
    }

    func deleteDraft(draftId: Int) {
        self.sendAuthorizedRequest(endpoint: "/mobile/api/v1/publication/" + String(draftId), method: .delete, parameters: [:]) { r in
//            completionHandler()
        }
    }

    func getSizes(categoryId: Int, completionHandler: @escaping ([Size]) -> ()) {
        self.sendAuthorizedRequest(endpoint: "/mobile/api/v1/catalog/sizes/" + String(categoryId) , method: .get, parameters: [:]) { responseObject, error in
            var sizes:[Size] = []
            let json = JSON(responseObject)
            for (index, j):(String, JSON) in json {
                let size = Size(type: j["sizeType"].stringValue, values: [])
                for (index, jj):(String, JSON) in j["values"] {
                    let value = SizeValue(id: jj["id"].intValue, name: jj["name"].stringValue)
                    size.values.append(value)
                }
                sizes.append(size)
            }
            completionHandler(sizes)
        }
    }

    private func addChildren(_ json: JSON, _ category: ProductCategory, _ appendSelf: Bool) {
        var children:[ProductCategory] = []
        print(json)
        if(appendSelf) {
            children.append(ProductCategory(id: category.id, name: category.name, children: [], displayInTreeName: "Все"))
        }
        for (index, j):(String, JSON) in json {
            let child = ProductCategory(id: j["id"].intValue, name: j["displayName"].stringValue, children: [], displayInTreeName: j["displayName"].stringValue)
            children.append(child)
            if let hasChildren = j["hasChildren"].bool {
                self.addChildren(j["children"], child, appendSelf && hasChildren);
            }
        }
        category.children.append(contentsOf: children)
    }

    private func sendAuthorizedRequest(endpoint: String, method: HTTPMethod, parameters: [String:Any], completionHandler: @escaping (Any?, Error?) -> ()) {
        sendAuthorizedRequest(endpoint: endpoint, method: method, parameters: parameters, completionHandler: completionHandler, errorHandler: {e in})
    }

    private func sendAuthorizedRequest(endpoint: String, method: HTTPMethod, parameters: [String:Any], completionHandler: @escaping (Any?, Error?) -> (), errorHandler: @escaping (String) -> (), needAuthorize: Bool) {
        var authorizedParameters: Parameters = [:]
        if(needAuthorize) {
            let email = GlobalProvider.instance.getAccountManager().getEmail()
            let password = GlobalProvider.instance.getAccountManager().getPassword()
            authorizedParameters = [
                "email": email,
                "password": password
            ]
        }
        authorizedParameters.update(other: parameters)
        Alamofire.request(ApiRequester.domain + endpoint, method: method, parameters: authorizedParameters).responseJSON { response in

            if let status = response.response?.statusCode {
                let value = response.result.value
                switch (status) {
                case 200:
                    print(value)
                    completionHandler(value as? Any, nil)
                case 400, 404, 500:
                    print(value)
                    let json = JSON(value)
                    let error = json["message"].string ?? "Произошла ошибка"
                    errorHandler(error)
                default:
                    print(value)
                }

            }
        }
    }

    private func sendAuthorizedRequest(endpoint: String, method: HTTPMethod, parameters: [String:Any], completionHandler: @escaping (Any?, Error?) -> (), errorHandler: @escaping (String) -> ()) {
        sendAuthorizedRequest(endpoint: endpoint, method: method, parameters: parameters, completionHandler: completionHandler, errorHandler: errorHandler, needAuthorize: true)
    }

    func thirdStep(image: UIImage, productId: Int, imageOrder: Int, completionHandler: @escaping (Draft) -> ()) {
        let imgData = UIImageJPEGRepresentation(image, 0.2)!

        let email = GlobalProvider.instance.getAccountManager().getEmail()
        let password = GlobalProvider.instance.getAccountManager().getPassword()

        let parameters = [
            "order": String(imageOrder),
            "productId": String(productId),
            "email": email,
            "password": password
        ]

        Alamofire.upload(multipartFormData: { multipartFormData in
            multipartFormData.append(imgData, withName: "image",fileName: "Draft-" + String(productId) + "-" + String(imageOrder) + ".jpg", mimeType: "image/jpg")
            for (key, value) in parameters {
                multipartFormData.append(value.data(using: String.Encoding.utf8)!, withName: key)
            }
        },
                to:ApiRequester.domain + "/mobile/api/v1/publication/step/3")
        { (result) in
            switch result {
            case .success(let upload, _, _):

                upload.uploadProgress(closure: { (progress) in
                    print("Upload Progress: \(progress.fractionCompleted)")
                })

                upload.responseJSON { response in
                    print(response.result.value)
                    completionHandler(Draft(json: JSON(response.result.value)))
                }

            case .failure(let encodingError):
                print(encodingError)
            }
        }
    }

    func publish(draft: Draft, completionHandler: @escaping (Draft, String?) -> (), completePublication: Bool) {
        let email = GlobalProvider.instance.getAccountManager().getEmail()
        let password = GlobalProvider.instance.getAccountManager().getPassword()
        var parameters = draft.dictionaryRepresentation()
        var authorizedParameters: Parameters = [
            "email": email,
            "password": password,
            "isCompletePublication": completePublication
        ]
        parameters.update(other: authorizedParameters)
        Alamofire.request(ApiRequester.domain + "/mobile/api/v1/publication/publish2?email=" + email + "&password=" + password , method: .put, parameters: parameters, encoding: JSONEncoding.default).responseJSON { response in
            if let status = response.response?.statusCode {
                let value = response.result.value as! NSDictionary
                switch(status) {
                case 200:
                    completionHandler(Draft(object: value), nil)
                case 400:
                    let json = JSON(value)
                    let error = json["message"].string
                    completionHandler(draft, error)
                case 500:
                    completionHandler(draft, "Произошла ошибка сайта")
                default:
                    print(print(value))
                }
            }
        }
    }

    func updateDeliveryAddress(orderId: Int, deliveryRequisite: DeliveryRequisite) {
        let email = GlobalProvider.instance.getAccountManager().getEmail()
        let password = GlobalProvider.instance.getAccountManager().getPassword()
        var parameters = deliveryRequisite.dictionaryRepresentation()
        var authorizedParameters: Parameters = [
            "email": email,
            "password": password
        ]
        parameters.update(other: authorizedParameters)
        Alamofire.request(ApiRequester.domain + "/mobile/api/v1/orders/" + String(orderId) + "/delivery" + "?email=" + email + "&password=" + password , method: .put, parameters: parameters, encoding: JSONEncoding.default).responseJSON { response in
            if let status = response.response?.statusCode {
                switch(status) {
                case 200:
                    print("Адрес доставки сохранен")
                case 500: //FIXME: нужно переделать на 400
                    let value = response.result.value as! NSDictionary
                    print(value)
                default:
                    print("default")
                }
            }
        }
    }

    func applyDiscount(orderId: Int, code: String, completionHandler: @escaping (Discount) -> (), errorHandler: @escaping (String) -> ()) {
        var params: Parameters = [
            "code": code
        ]
        self.sendAuthorizedRequest(endpoint: "/mobile/api/v1/orders/" + String(orderId) + "/discount", method: .post, parameters: params, completionHandler: {r, e in
            completionHandler(Discount(object: r))
        }, errorHandler: {e in
            errorHandler(e)
        })
    }

    func getCart(completionHandler: @escaping (Cart) -> ()) {
        self.sendAuthorizedRequest(endpoint: "/mobile/api/v1/cart", method: .get, parameters: [:]) { response, error in
            let json = JSON(response)
            let cart = Cart(json: json)
            completionHandler(cart)
        }
    }

    func addItemToCart(productId: Int, sizeId: Int, price: Float, completionHandler: @escaping () -> ()) {
        var params: Parameters = [
            "productId": productId,
            "sizeId": sizeId,
            "price": price
        ]
        self.sendAuthorizedRequest(endpoint: "/mobile/api/v1/cart", method: .post, parameters: params, completionHandler: {r, e in
            completionHandler()
        })
    }

    func deleteProduct(productId: Int,  completionHandler: @escaping () -> ()) {
        self.sendAuthorizedRequest(endpoint: "/mobile/api/v1/product/" + String(productId), method: .delete, parameters: [:], completionHandler: {r, e in
            completionHandler()
        })
    }

    func deleteCartItem(cartItemId: Int, completionHandler: @escaping () -> ()) {
        self.sendAuthorizedRequest(endpoint: "/mobile/api/v1/cart/" + String(cartItemId), method: .delete, parameters: [:]) { r in
            completionHandler()
        }
    }

    func createOrder(cartItems: [Int], completionHandler: @escaping (Order) -> ()) {
        var parameters = [
            "cartItems": cartItems.map({String($0)}).joined(separator: ",")
        ]
        self.sendAuthorizedRequest(endpoint: "/mobile/api/v1/cart/order2", method: .post, parameters: parameters, completionHandler: {response, error in
            let json = JSON(response)
            completionHandler(Order(json: json))
        })
    }

    func getOrder(orderId: Int, completionHandler: @escaping (Order) -> ()) {
        self.sendAuthorizedRequest(endpoint: "/mobile/api/v1/orders/" + String(orderId), method: .get, parameters: [:], completionHandler: {response, error in
            let json = JSON(response)
            completionHandler(Order(json: json))
        })
    }

    func initHold(orderId: Int, completionHandler: @escaping (String) -> ()) {
        self.sendAuthorizedRequest(endpoint: "/mobile/api/v1/orders/" + String(orderId) + "/hold", method: .post, parameters: [:], completionHandler: {response, error in
            let json = JSON(response)
            if let v = json["paymentRequest"].string {
                completionHandler(v)
            }
        })
    }

    func hold(paymentRequest: PaymentRequest, webView: UIWebView, need3DSHandler: @escaping (Any) -> (), successHandler: @escaping (String) -> ()) {
        var parameters = paymentRequest.dictionaryRepresentation()
        Alamofire.request(ApiRequester.bankEndpoint + "/rest/v1/hold" , method: .post, parameters: parameters, encoding: JSONEncoding.default).responseJSON { response in
            if let status = response.response?.statusCode {
                if let value = response.result.value {
                    print(value)
                    switch (status) {
                    case 200:
                        let json = JSON(value)
                        if let type = json["status"]["type"].string {
                            successHandler(type)
                        }
                    case 420:
                        need3DSHandler(value) // здесь идет кештрование полученных данных в сущность PaymentRequest
                        self.send3DSRequest(value: value, webView: webView)
                    default:
                        print(response)
                    }
                }
            }
        }
    }

    public func send3DSRequest(value: Any, webView: UIWebView) {
        let json = JSON(value)
        var acs_url = json["tds_request"]["acs_url"].stringValue
        let parameters = [
            "PaReq": json["tds_request"]["pa_req"].stringValue,
            "MD": json["tds_request"]["md"].stringValue,
            "TermUrl": "http://vl.ru"
        ]
        Alamofire.request(acs_url, method: .post, parameters: parameters).responseString { response in
            let html = response.result.value!
            webView.isHidden = false
            webView.loadHTMLString(html, baseURL: URL(string: acs_url))
        }
    }

    public func confirmPayment(d3SecureData: Any, paRes: String, completeHandler: @escaping (String) -> ()) {
        let json = JSON(d3SecureData)
        let parameters = [
            "pa_res": paRes,
            "md": json["tds_request"]["md"].stringValue
        ]

        Alamofire.request(json["confirm_url"].stringValue , method: .post, parameters: parameters, encoding: JSONEncoding.default).responseJSON { response in
            if let status = response.response?.statusCode {
                if let value = response.result.value {
                    print(value)
                    switch (status) {
                    case 200:
                        let json = JSON(value)
                        if let type = json["status"]["type"].string {
                            completeHandler(type)
                        }

                    default:
                        print("default")
                    }
                }
            }
        }
    }

    public func getProduct(productId: Int, completeHandler: @escaping (Product) -> ()) {
        self.sendAuthorizedRequest(endpoint: "/mobile/api/v1/product/" + String(productId), method: .get, parameters: [:], completionHandler: {response, error in
            completeHandler(Product(object: response))
        })
    }

    public func getProductComments(productId: Int, completeHandler: @escaping ([Comment]) -> ()) {
        self.sendAuthorizedRequest(endpoint: "/mobile/api/v1/product/" + String(productId) + "/comments", method: .get, parameters: [:], completionHandler: {response, error in
            var items: [Comment] = []
            let json = JSON(response)
            for (index,subJson):(String, JSON) in json {
                let item = Comment(json: subJson)
                items.append(item)
            }
            completeHandler(items)
        })
    }

    func publishProductComment(productId: Int, text: String, completeHandler: @escaping (Comment) -> ()) {
        let parameters = [
            "text": text
        ]
        self.sendAuthorizedRequest(endpoint: "/mobile/api/v1/product/" + String(productId) + "/comment", method: .post, parameters: parameters, completionHandler: { response, error in
            let json = JSON(response)
            for (index,subJson):(String, JSON) in json {
                let item = Comment(json: subJson)
                completeHandler(item)
            }
        })
    }

    public func saveAccount(account: Account) {
        let email = GlobalProvider.instance.getAccountManager().getEmail()
        let password = GlobalProvider.instance.getAccountManager().getPassword()
        var parameters = account.dictionaryRepresentation()
        var authorizedParameters: Parameters = [
            "email": email,
            "password": password
        ]
        parameters.update(other: authorizedParameters)
        Alamofire.request(ApiRequester.domain + "/mobile/api/v1/account?email=" + email + "&password=" + password , method: .put, parameters: parameters, encoding: JSONEncoding.default).responseJSON { response in
            if let status = response.response?.statusCode {
                switch(status) {
                case 200:
                    print("Аккаунт успешно сохранен")
                default:
                    print("Ошибка при сохранении аккаунта")
                }
            }
        }
    }

    public func confirmSale(saleId: Int, sellerRequisite: SellerRequisite, doConfirmSale: Bool, completeHandler: @escaping () -> (), errorHandler: @escaping () -> ()) {
        let email = GlobalProvider.instance.getAccountManager().getEmail()
        let password = GlobalProvider.instance.getAccountManager().getPassword()
        var parameters = sellerRequisite.dictionaryRepresentation()
        var authorizedParameters: Parameters = [
            "email": email,
            "password": password
        ]
        parameters.update(other: authorizedParameters)
        Alamofire.request(ApiRequester.domain + "/mobile/api/v1/account/sales/confirm/" + String(saleId) + "?email=" + email + "&password=" + password + "&doConfirmSale=" + String(doConfirmSale), method: .put, parameters: parameters, encoding: JSONEncoding.default).responseJSON { response in
            if let status = response.response?.statusCode {
                switch(status) {
                case 200:
                    completeHandler()
                default:
                    print("Ошибка подтверждения продажи")
                    errorHandler()
                }
            }
        }
    }

    func getMyOrders(completeHandler: @escaping ([Order]) -> ()) {
        self.sendAuthorizedRequest(endpoint: "/mobile/api/v1/account/orders", method: .get, parameters: [:]) { responseObject, error in
            var orders: [Order] = []
            let json = JSON(responseObject)
            for (index,subJson):(String, JSON) in json {
                let order = Order(json: subJson)
                orders.append(order)
            }
            completeHandler(orders)
        }
    }

    func makeOffer(productId: Int, price: String, completeHandler: @escaping (Offer) -> (), errorHandler: @escaping (String) -> ()) {

        var params: Parameters = [
            "price": price
        ]

        self.sendAuthorizedRequest(endpoint: "/mobile/api/v1/offers/product/" + String(productId), method: .post, parameters: params, completionHandler: {r, e in
            completeHandler(Offer(object: r))
        }, errorHandler: {e in
            errorHandler(e)
        })
    }

    func getMyNews(completeHandler: @escaping ([Notification]) -> ()) {
        self.sendAuthorizedRequest(endpoint: "/mobile/api/v1/account/news", method: .get, parameters: [:], completionHandler: {response, error in
            var items: [Notification] = []
            let json = JSON(response)
            for (index,subJson):(String, JSON) in json {
                let item = Notification(json: subJson)
                items.append(item)
            }
            completeHandler(items)
        })
    }

    func getNews(profileId: Int, completeHandler: @escaping ([Notification]) -> ()) {
        self.sendAuthorizedRequest(endpoint: "/mobile/api/v1/profile/news/" + String(profileId), method: .get, parameters: [:], completionHandler: {response, error in
            var items: [Notification] = []
            let json = JSON(response)
            for (index,subJson):(String, JSON) in json {
                let item = Notification(json: subJson)
                items.append(item)
            }
            completeHandler(items)
        })
    }

    func getMyNotifications(completeHandler: @escaping ([Notification]) -> ()) {
        self.sendAuthorizedRequest(endpoint: "/mobile/api/v1/account/notifications", method: .get, parameters: [:], completionHandler: {response, error in
            var items: [Notification] = []
            let json = JSON(response)
            for (index,subJson):(String, JSON) in json {
                let item = Notification(json: subJson)
                items.append(item)
            }
            completeHandler(items)
        })
    }

    func readAllNotifications() {
        self.sendAuthorizedRequest(endpoint: "/mobile/api/v1/account/notifications", method: .put, parameters: [:], completionHandler: {response, error in
        })
    }

    func getPriceWithoutCommission(productId: Int, price: String, completeHandler: @escaping (Float) -> ()) {
        var params: Parameters = [
            "price": price
        ]
        self.sendAuthorizedRequest(endpoint: "/mobile/api/v1/publication/commission/" + String(productId), method: .get, parameters: params, completionHandler: {response, error in
            completeHandler(JSON(response).floatValue)
        })
    }

    func getAccountOffers(offersToMe: Bool, completeHandler: @escaping ([AccountOffer]) -> ()) {
        self.sendAuthorizedRequest(endpoint: offersToMe ? "/mobile/api/v1/account/offers-to-me2" : "/mobile/api/v1/account/offers2", method: .get, parameters: [:], completionHandler: {response, error in
            var items: [AccountOffer] = []
            let json = JSON(response)
            for (index,subJson):(String, JSON) in json {
                let item = AccountOffer(json: subJson)
                items.append(item)
            }
            completeHandler(items)
        })
    }

    func getAccountOffer(offerId: Int, completeHandler: @escaping (AccountOffer) -> ()) {
        self.sendAuthorizedRequest(endpoint: "/mobile/api/v1/account/offers-to-me2/" + String(offerId), method: .get, parameters: [:], completionHandler: {response, error in
            completeHandler(AccountOffer(object: response))
        })
    }

    func confirmAccountOffer(offerId: Int, doConfirm: Bool, completeHandler: @escaping (AccountOffer) -> (), errorHandler: @escaping (String) -> ()) {
        var params: Parameters = [
            "confirm": doConfirm
        ]
        self.sendAuthorizedRequest(endpoint: "/mobile/api/v1/account/offers2/" + String(offerId), method: .post, parameters: params, completionHandler: {response, error in
            completeHandler(AccountOffer(object: response))
        }, errorHandler: { e in
            errorHandler(e)
        })
    }

    func updateAvatar(image: UIImage, completeHandler: @escaping () -> (), errorHandler: @escaping (String) -> ()) {
        let imgData = UIImageJPEGRepresentation(image, 0.2)!

        let email = GlobalProvider.instance.getAccountManager().getEmail()
        let password = GlobalProvider.instance.getAccountManager().getPassword()

        let parameters = [
            "email": email,
            "password": password
        ]

        Alamofire.upload(multipartFormData: { multipartFormData in
            multipartFormData.append(imgData, withName: "image",fileName: "avatar-" + email + ".jpg", mimeType: "image/jpg")
            for (key, value) in parameters {
                multipartFormData.append(value.data(using: String.Encoding.utf8)!, withName: key)
            }
        },
                to:ApiRequester.domain + "/mobile/api/v1/account/image")
        { (result) in
            switch result {
            case .success(let upload, _, _):

                upload.uploadProgress(closure: { (progress) in
                    print("Upload Progress: \(progress.fractionCompleted)")
                })

                upload.responseJSON { response in
                    if let status = response.response?.statusCode {
                        let value = response.result.value
                        switch (status) {
                        case 200:
                            completeHandler()
                        case 400, 404, 500:
                            print(value)
                            let json = JSON(value)
                            let error = json["message"].string ?? "Произошла ошибка"
                            errorHandler(error)
                        default:
                            errorHandler("Произошла ошибка")
                        }
                    }
                }
            case .failure(let encodingError):
                print(encodingError)
            }
        }
    }
}