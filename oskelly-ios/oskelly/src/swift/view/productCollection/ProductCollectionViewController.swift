//
// Created by Виталий Хлудеев on 12.05.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class ProductCollectionViewController: UICollectionViewController, UICollectionViewDelegateFlowLayout {

    let refresher = UIRefreshControl()

    var isRefresing: Bool = false

    var existsMore: Bool = true

    var currentPage = 1;

    var pagesCount: Int = 0

    let minWidth = min(UIScreen.main.bounds.width, UIScreen.main.bounds.height)

    override func viewDidLoad() {
        super.viewDidLoad()
        if (GlobalProvider.instance.getAccountManager().isUserLoggedIn()) {
            GlobalProvider.instance.cartIconService.addCartIcon(self)
        }
        collectionView!.delegate = self
        collectionView!.dataSource = self
        collectionView!.register(ProductCollectionItemCell.self, forCellWithReuseIdentifier: "collectionCell")
        collectionView!.backgroundColor = UIColor.white
        collectionView!.isScrollEnabled = true
        collectionView!.isUserInteractionEnabled = true

        collectionView!.alwaysBounceVertical = true
        refresher.addTarget(self, action: #selector(loadData), for: UIControlEvents.valueChanged)
        collectionView!.addSubview(refresher)
        if let collectionViewFlowLayout = collectionView!.collectionViewLayout as? UICollectionViewFlowLayout {
            collectionViewFlowLayout.footerReferenceSize = CGSize(width: self.view.bounds.width, height: 50)
        }
        collectionView!.register(CatalogViewDirRefresherCell.self, forSupplementaryViewOfKind: UICollectionElementKindSectionFooter, withReuseIdentifier: "collectionRefresfer")
        self.loadData()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    var collectionDataSource : [ProductCollectionItem] = []

    override func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return collectionDataSource.count
    }

    override func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell
    {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "collectionCell", for: indexPath) as! ProductCollectionItemCell
        cell.render(item: collectionDataSource[indexPath.row])
        return cell
    }

    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {

        var proportion: CGFloat
        if(UIDevice.current.userInterfaceIdiom == UIUserInterfaceIdiom.pad) {
            proportion = 5
        }
        else {
            proportion = 3
        }
        let totalWidth: CGFloat = minWidth / proportion
        let totalHeight: CGFloat = totalWidth * 2.1
        return CGSize(width: totalWidth, height: totalHeight)
    }

    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumLineSpacingForSectionAt section: Int) -> CGFloat {
        return 1;
    }

    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumInteritemSpacingForSectionAt section: Int) -> CGFloat {
        return 0;
    }

    override func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        if (!GlobalProvider.instance.getAccountManager().isUserLoggedIn()) {
            let storyboard = UIStoryboard(name: "Main", bundle: nil)
            let vc = storyboard.instantiateViewController(withIdentifier: "RegistrationViewController") as! RegistrationViewController
            self.present(vc, animated: true, completion: nil)
            return
        }
        let productId = collectionDataSource[indexPath.row].id!
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        let c = storyboard.instantiateViewController(withIdentifier: "ProductViewController") as! ProductViewController
        c.productId = productId
        self.navigationController?.pushViewController(c, animated: true)
    }

    func stopRefresher()
    {
        refresher.endRefreshing()
    }

    override func collectionView(_ collectionView: UICollectionView, viewForSupplementaryElementOfKind kind: String, at indexPath: IndexPath) -> UICollectionReusableView {

        switch kind {

        case UICollectionElementKindSectionFooter:
            let footerView = collectionView.dequeueReusableSupplementaryView(ofKind: kind, withReuseIdentifier: "collectionRefresfer", for: indexPath) as! CatalogViewDirRefresherCell
            if (self.existsMore) {
                footerView.showRefresher()
            }
            else {
                footerView.hideRefresher()
            }
            return footerView

        default:
            assert(false, "Unexpected element kind")
        }
        return collectionView.dequeueReusableSupplementaryView(ofKind: kind, withReuseIdentifier: "", for: indexPath)
    }

    override func scrollViewDidScroll(_ scrollView: UIScrollView) {
        let offsetY = scrollView.contentOffset.y
        let contentHeight = scrollView.contentSize.height

        if offsetY > contentHeight - scrollView.frame.size.height - scrollView.frame.size.height * 0.5 && !self.isRefresing && self.existsMore {
            self.isRefresing = true
            currentPage = currentPage + 1
            loadProductsFromServer() { content in
                self.collectionDataSource.append(contentsOf: content)
                self.existsMore = self.pagesCount > self.currentPage
                self.collectionView!.reloadData()
                self.isRefresing = false
            }
        }
    }

    func loadProductsFromServer(completionHandler: @escaping ([ProductCollectionItem]) -> ()) {
        let r = ProductRequest()
        r.page = currentPage
        r.category = 2
        GlobalProvider.instance.getApiRequester().getProducts(productRequest: r) { page in
            if let count = page.totalPagesCount {
                self.pagesCount = count
            }
            else {
                self.pagesCount = 1
            }
            completionHandler(page.items!)
        }
    }

    func loadData()
    {
        self.currentPage = 1
        loadProductsFromServer() { content in
            self.collectionDataSource.removeAll()
            self.collectionDataSource.append(contentsOf: content)
            if(self.pagesCount <= 1) {
                self.existsMore = false
            }
            else {
                self.existsMore = true
            }
            self.collectionView!.reloadData()
            self.stopRefresher()
        }
    }

    func clearData() {
        self.collectionDataSource.removeAll()
        self.collectionView!.reloadData()
        self.existsMore = true
        self.stopRefresher()
    }
}
