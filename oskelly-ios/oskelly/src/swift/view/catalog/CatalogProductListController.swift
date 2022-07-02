//
// Created by Виталий Хлудеев on 25.05.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class CatalogProductListController: ProductCollectionViewController {

    var catalogName = ""
    var categoryId : Int?
    var request = ProductRequest()
    var attributes : [Attribute] = []
    var sizes : [Size] = []
    var conditions : [ProductCondition] = []

    override func viewDidLoad() {
        super.viewDidLoad()
        self.navigationItem.title = catalogName
        collectionView!.register(CatalogProductListHeader.self, forSupplementaryViewOfKind: UICollectionElementKindSectionHeader, withReuseIdentifier: "header")
        if let collectionViewFlowLayout = collectionView!.collectionViewLayout as? UICollectionViewFlowLayout {
            collectionViewFlowLayout.sectionHeadersPinToVisibleBounds = true
        }
        if let c = self.categoryId {
            GlobalProvider.instance.getApiRequester().getAttributes(categoryId: c) { attributes in
                self.attributes = attributes
            }
            GlobalProvider.instance.getApiRequester().getSizes(categoryId: c) { sizes in
                self.sizes = sizes
            }
        }
        GlobalProvider.instance.directoriesProvider.getConditions(completionHandler: {conditions in
            self.conditions = conditions
        })
    }

    override func loadProductsFromServer(completionHandler: @escaping ([ProductCollectionItem]) -> ()) {
        request.page = currentPage
        request.category = categoryId
        GlobalProvider.instance.getApiRequester().getProducts(productRequest: request) { page in
            if let count = page.totalPagesCount {
                self.pagesCount = count
            }
            else {
                self.pagesCount = 1
            }
            completionHandler(page.items!)
        }
    }

    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, referenceSizeForHeaderInSection section: Int) -> CGSize {
        return CGSize(width: minWidth, height: 47.0)
    }

    override func collectionView(_ collectionView: UICollectionView, viewForSupplementaryElementOfKind kind: String, at indexPath: IndexPath) -> UICollectionReusableView {

        switch kind {

        case UICollectionElementKindSectionHeader:
            let header = collectionView.dequeueReusableSupplementaryView(ofKind: kind, withReuseIdentifier: "header", for: indexPath) as! CatalogProductListHeader
            header.render(controller: self)
            return header

        case UICollectionElementKindSectionFooter:
            return super.collectionView(collectionView, viewForSupplementaryElementOfKind: kind, at: indexPath)

        default:
            assert(false, "Unexpected element kind")
        }
        return collectionView.dequeueReusableSupplementaryView(ofKind: kind, withReuseIdentifier: "", for: indexPath)
    }
}
