//
// Created by Виталий Хлудеев on 11.11.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class ProfileProductsController : ProductCollectionViewController {

    var profile: PublicProfile?

    var profileId: Int?

    var selectedIndex: Int = 0

//    var cachedProducts : [ProductCollectionItem] = []
//    var cachedWishList : [ProductCollectionItem] = []
//    var cachedFavourites : [ProductCollectionItem] = []

    override func viewDidLoad() {
        if(profile == nil && profileId == nil) {
            GlobalProvider.instance.profileProvider.getCurrentProfile(completionHandler: { p in
                self.profile = p
                super.viewDidLoad()
            })
        }
        else if(profile == nil && profileId != nil) {
            GlobalProvider.instance.getApiRequester().getProfile(profileId: profileId!, completionHandler: { p in
                self.profile = p
                super.viewDidLoad()
            })
        }
        else {
            super.viewDidLoad()
        }
        collectionView!.register(ProfileHeader.self, forSupplementaryViewOfKind: UICollectionElementKindSectionHeader, withReuseIdentifier: "header")
        collectionView!.register(UICollectionViewCell.self, forSupplementaryViewOfKind: UICollectionElementKindSectionFooter, withReuseIdentifier: "footer")
        navigationItem.title = "Профиль"
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        if let p = self.profile {
            GlobalProvider.instance.getApiRequester().getProfile(profileId: p.id, completionHandler: { pp in
                self.profile = pp
                self.collectionView?.reloadData()
            })
        }
    }

    override func collectionView(_ collectionView: UICollectionView, viewForSupplementaryElementOfKind kind: String, at indexPath: IndexPath) -> UICollectionReusableView {

        switch kind {

        case UICollectionElementKindSectionHeader:
            let headerView = collectionView.dequeueReusableSupplementaryView(ofKind: kind, withReuseIdentifier: "header", for: indexPath) as! ProfileHeader
            if(profile != nil) {
                headerView.render(profile: profile!, controller: self, profileNewsController: nil)
            }
            return headerView

        case UICollectionElementKindSectionFooter:
            return super.collectionView(collectionView, viewForSupplementaryElementOfKind: kind, at: indexPath)
        default:
            assert(false, "Unexpected element kind")
        }
        return collectionView.dequeueReusableSupplementaryView(ofKind: kind, withReuseIdentifier: "", for: indexPath)
    }

    override func loadProductsFromServer(completionHandler: @escaping ([ProductCollectionItem]) -> ()) {
        switch selectedIndex {
            case 0:
                loadProducts(completionHandler: completionHandler)
            case 1:
                let c = storyboard!.instantiateViewController(withIdentifier: "ProfileNewsController") as! ProfileNewsController
                c.profile = profile
                c.parentController = self
                navigationController?.pushViewController(c, animated: false)
                navigationController.map({
                    $0.viewControllers.remove(at: $0.viewControllers.count - 2)
                })
            case 2:
                loadWishList(completionHandler: completionHandler)
            case 3:
                loadFavourites(completionHandler: completionHandler)
            default:
                print("Not implemented yet")
        }
    }

    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, referenceSizeForHeaderInSection section: Int) -> CGSize{
        return CGSize(width: 100, height: 150 + ProfileHeader.scrollViewHeight)
    }

    func loadProducts(completionHandler: @escaping ([ProductCollectionItem]) -> ()) {
        let request = ProductRequest()
        request.seller = profileId ?? profile?.id
        request.page = currentPage
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

    func loadWishList(completionHandler: @escaping ([ProductCollectionItem]) -> ()) {
        GlobalProvider.instance.getApiRequester().getWishList(page: currentPage, profileId: profile!.id) { page in
            completionHandler(page.items!)
            self.existsMore = false
        }
    }

    func loadFavourites(completionHandler: @escaping ([ProductCollectionItem]) -> ()) {
        GlobalProvider.instance.getApiRequester().getFavourites(page: currentPage, profileId: profile!.id) { page in
            completionHandler(page.items!)
            self.existsMore = false
        }
    }
}