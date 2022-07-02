//
// Created by Виталий Хлудеев on 13.02.18.
// Copyright (c) 2018 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class AccountPriceFollowingController : ProductCollectionViewController {

    override func loadProductsFromServer(completionHandler: @escaping ([ProductCollectionItem]) -> ()) {
        self.existsMore = false
        GlobalProvider.instance.getApiRequester().getPriceFollowings() { page in
            completionHandler(page.items!)
        }
    }
}