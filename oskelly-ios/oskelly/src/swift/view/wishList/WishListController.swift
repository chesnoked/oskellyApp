//
// Created by Виталий Хлудеев on 01.12.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class WishListController : ProductCollectionViewController {

    var profileId : Int!

    override func viewDidLoad() {
        super.viewDidLoad()
        navigationItem.title = "Wish List"
    }

    override func loadProductsFromServer(completionHandler: @escaping ([ProductCollectionItem]) -> ()) {
        // TODO: залечить крутилку
        self.existsMore = false
        GlobalProvider.instance.getApiRequester().getWishList(page: currentPage, profileId: profileId) { page in
            completionHandler(page.items!)
        }
    }
}