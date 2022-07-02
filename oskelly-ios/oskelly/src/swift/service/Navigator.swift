//
// Created by Виталий Хлудеев on 08.01.18.
// Copyright (c) 2018 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit

class Navigator {

    func navigateToProfile(profileId: Int, controller: UIViewController?) {
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        let c = storyboard.instantiateViewController(withIdentifier: "ProfileProductsController") as! ProfileProductsController
        c.profileId = profileId
        controller?.navigationController?.pushViewController(c, animated: true)
    }

    func navigateToProduct(productId: Int, controller: UIViewController?) {
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        let c = storyboard.instantiateViewController(withIdentifier: "ProductViewController") as! ProductViewController
        c.productId = productId
        controller?.navigationController?.pushViewController(c, animated: true)
    }

    func navigateToNotificationTarget(notification: Notification, controller: UIViewController) {
        if let url = notification.urlOfTargetObject {
            if(url.starts(with: "/profile/")) {
                let c = controller.storyboard!.instantiateViewController(withIdentifier: "ProfileProductsController") as! ProfileProductsController
                c.profileId = (url.replace(target: "/profile/", withString: "") as NSString).integerValue
                controller.navigationController?.pushViewController(c, animated: true)
            }
            else if(url.starts(with: "/products/")) {
                let c = controller.storyboard!.instantiateViewController(withIdentifier: "ProductViewController") as! ProductViewController
                c.productId = (url.replace(target: "/products/", withString: "") as NSString).integerValue
                controller.navigationController?.pushViewController(c, animated: true)
            }
            else if(url.starts(with: "/account/offers/")) {
                let c = controller.storyboard!.instantiateViewController(withIdentifier: "AccountSingleOfferController") as! AccountSingleOfferController
                c.offerId = (url.replace(target: "/account/offers/", withString: "") as NSString).integerValue
                controller.navigationController?.pushViewController(c, animated: true)
            }
            else if(url.starts(with: "/account/orders")) {
//                let c = controller.storyboard!.instantiateViewController(withIdentifier: "AccountOrderController") as! AccountOrderController
//                c.orderId = (url.replace(target: "/account/orders/", withString: "") as NSString).integerValue
//                controller.navigationController?.pushViewController(c, animated: true)

                let c = controller.storyboard!.instantiateViewController(withIdentifier: "AccountOrderListController") as! AccountOrderListController
                controller.navigationController?.pushViewController(c, animated: true)
            }
            else if(url.starts(with: "/account/products/items/") && url.contains("/sale-confirmation")) {
                let c = controller.storyboard!.instantiateViewController(withIdentifier: "SaleController") as! SaleController
                c.productItemId = (url.replace(target: "/account/products/items/", withString: "").replace(target: "/sale-confirmation", withString: "") as NSString).integerValue
                controller.navigationController?.pushViewController(c, animated: true)
            }
            else {
                let url = URL(string: ApiRequester.domain + url)!
                UIApplication.shared.open(url, options: [:])
            }
        }
    }
}

extension String {

    func contains(_ find: String) -> Bool{
        return self.range(of: find) != nil
    }

    func containsIgnoringCase(_ find: String) -> Bool{
        return self.range(of: find, options: .caseInsensitive) != nil
    }
}