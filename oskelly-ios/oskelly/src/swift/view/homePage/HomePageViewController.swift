//
//  HomePageViewController.swift
//  oskelly
//
//  Created by Виталий Хлудеев on 02.05.17.
//  Copyright © 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import ImageSlideshow
import AlamofireImage
import QuartzCore
import Alamofire
import SnapKit

class HomePageViewController : ProductCollectionViewController, UISearchBarDelegate, UINavigationControllerDelegate, UIImagePickerControllerDelegate {

    let logoImageView = UIImageView()
    let searchButton = UIButton(type: .custom)
    var searchBar: UISearchBar = UISearchBar(frame: CGRect(x: 0, y: 0, width: min(UIScreen.main.bounds.width, UIScreen.main.bounds.height) - 50, height: 10))

    let galleryDataSource = ["https://oskelly.ru/img/promo/gallery/c00f644b-cf09-4826-b8c6-b1435438163a", "https://oskelly.ru/img/promo/gallery/5c13d4fb-f0b2-4d9b-aae2-5936259dad34"]

    var itemsCount: Int?

    override func viewDidLoad() {
        super.viewDidLoad()
        if let collectionViewFlowLayout = collectionView!.collectionViewLayout as? UICollectionViewFlowLayout {
            collectionViewFlowLayout.headerReferenceSize = CGSize(width: minWidth, height: minWidth / 1.2)
            collectionViewFlowLayout.footerReferenceSize = CGSize(width: minWidth, height: 95)
        }
        collectionView!.register(HomePageHeaderViewCell.self, forSupplementaryViewOfKind: UICollectionElementKindSectionHeader, withReuseIdentifier: "headerCell")
        collectionView!.register(HomePageFooterViewCell.self, forSupplementaryViewOfKind: UICollectionElementKindSectionFooter, withReuseIdentifier: "footerCell")

        logoImageView.contentMode = .scaleAspectFit
        logoImageView.image = UIImage(named: "assets/images/navigation/Logo.png")
        logoImageView.snp.makeConstraints { (make) -> Void in
            make.height.equalTo(logoImageView)
        }

        if (GlobalProvider.instance.getAccountManager().isUserLoggedIn()) {
            hideSerachBar()
        }
        else {
            navigationItem.titleView = logoImageView
            navigationItem.rightBarButtonItem = UIBarButtonItem(title: "Войти", style: UIBarButtonItemStyle.plain, target: self, action: #selector(self.entrance))
        }

        DispatchQueue.main.async {
            _ = DSCameraHandler2(delegate_: self)
        }

//        GlobalProvider.instance.getApiRequester().getOrder(orderId: 1080269, completionHandler: {o in
//            let c = self.storyboard?.instantiateViewController(withIdentifier: "OrderSummaryController") as! OrderSummaryController
//            c.order = o
//            self.navigationController?.pushViewController(c, animated: true)
//
//            GlobalProvider.instance.orderProvider.setCurrent(order: o)
//
////            let c = self.storyboard?.instantiateViewController(withIdentifier: "AccountOrderController") as! AccountOrderController
////            c.order = o
////            self.navigationController?.pushViewController(c, animated: true)
//        })
//        let c = self.storyboard?.instantiateViewController(withIdentifier: "PublicationSubmitBrandController") as! PublicationSubmitBrandController
//        self.navigationController?.pushViewController(c, animated: true)
    }

    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        if (GlobalProvider.instance.getAccountManager().isUserLoggedIn()) {
            hideSerachBar() // иначе баг на следующем экране
        }
    }

    private func hideSerachBar() {
        navigationItem.titleView = logoImageView
        addSearchButton()
        GlobalProvider.instance.cartIconService.addCartIcon(self)
        searchBar.text = nil
    }

    private func addSearchBar() {
        searchBar.placeholder = "Search"
        searchBar.barTintColor = .black
        searchBar.tintColor = .gray
        searchBar.sizeToFit()
        searchBar.delegate = self
        navigationItem.rightBarButtonItem = UIBarButtonItem(title: "Отмена", style: UIBarButtonItemStyle.plain, target: self, action: #selector(self.cancelButtonPressed))
        navigationItem.titleView = searchBar
        navigationItem.setLeftBarButtonItems(nil, animated: false)

        for subView in searchBar.subviews {

            for subViewOne in subView.subviews {

                if let textField = subViewOne as? UITextField {

                    subViewOne.backgroundColor = .black
                    textField.textColor = .gray
                    textField.font = MediumFont.systemFont(ofSize: 13)
                    //use the code below if you want to change the color of placeholder
                    let textFieldInsideUISearchBarLabel = textField.value(forKey: "placeholderLabel") as? UILabel
                    textFieldInsideUISearchBarLabel?.textColor = .gray
                }
            }
        }
    }

    private func addSearchButton() {
        searchButton.setImage(UIImage(named: "assets/images/navigation/Search.png"), for: .normal)
        searchButton.imageView?.contentMode = .scaleAspectFit
        let searchItem = UIBarButtonItem(customView: searchButton)
        self.navigationItem.setLeftBarButtonItems([searchItem], animated: false)
        searchButton.snp.makeConstraints { (make) -> Void in
            make.height.equalTo(searchButton)
        }
        searchButton.addTarget(self, action: #selector(self.searchButtonClicked(_:)), for: .touchUpInside)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    override func collectionView(_ collectionView: UICollectionView, viewForSupplementaryElementOfKind kind: String, at indexPath: IndexPath) -> UICollectionReusableView {

        switch kind {

        case UICollectionElementKindSectionHeader:
            let headerView = collectionView.dequeueReusableSupplementaryView(ofKind: kind, withReuseIdentifier: "headerCell", for: indexPath) as! HomePageHeaderViewCell
            headerView.render(urls: galleryDataSource, itemsCount: itemsCount)
            return headerView

        case UICollectionElementKindSectionFooter:
            let footerView = collectionView.dequeueReusableSupplementaryView(ofKind: kind, withReuseIdentifier: "footerCell", for: indexPath) as! HomePageFooterViewCell
            footerView.render(controller: self)
            return footerView

        default:
            assert(false, "Unexpected element kind")
        }
        return collectionView.dequeueReusableSupplementaryView(ofKind: kind, withReuseIdentifier: "collectionRefresfer", for: indexPath)
    }
    
    //TODO: Вынести в отдельный класс
    func cartButtonClicked(_ sender: AnyObject?) {
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        let vc = storyboard.instantiateViewController(withIdentifier: "CartViewController") as! CartViewController
        self.navigationController?.pushViewController(vc, animated: true)
    }

    func searchButtonClicked(_ sender: AnyObject?) {
        addSearchBar()
    }

    override func loadProductsFromServer(completionHandler: @escaping ([ProductCollectionItem]) -> ()) {
        GlobalProvider.instance.getApiRequester().getNewArrivals() { page in
            completionHandler(page.items!)
            self.pagesCount = 1
            self.existsMore = false
            self.itemsCount = page.totalItemsCount ?? 0
        }
    }

    func searchBarSearchButtonClicked(_ searchBar: UISearchBar) {
        let c = storyboard?.instantiateViewController(withIdentifier: "CatalogSearchController") as! CatalogSearchController
        c.search = searchBar.text
        navigationController?.pushViewController(c, animated: true)
    }

    func cancelButtonPressed() {
        hideSerachBar()
    }

    func entrance() {
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        let vc = storyboard.instantiateViewController(withIdentifier: "RegistrationViewController") as! RegistrationViewController
        self.present(vc, animated: true, completion: nil)
        return
    }
}
