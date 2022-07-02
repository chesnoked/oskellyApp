//
// Created by Виталий Хлудеев on 24.05.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class CatalogViewController : UIViewController, UITableViewDataSource, UITableViewDelegate {

    let tableView = UITableView()

    var parentCategory: ProductCategory?  = nil

    var needLoadTree = true

    var dataSource : [ProductCategory] = []

    override func viewDidLoad() {
        super.viewDidLoad()
        GlobalProvider.instance.cartIconService.addCartIcon(self)
        tableView.tableFooterView = UIView()
        tableView.dataSource = self
        tableView.delegate = self
        tableView.register(CatalogRow.self, forCellReuseIdentifier: "CatalogRow")
        tableView.register(CatalogRowHeader.self, forCellReuseIdentifier: "CatalogRowHeader")
        tableView.separatorStyle = .none
        self.view.addSubview(tableView)
        tableView.snp.makeConstraints { (make) -> Void in
            make.edges.equalTo(view.snp.edges)
        }
        getCategoriesIfNeed()
        needLoadTree = false
        navigationItem.title = "Выберите категорию"
    }

    private func getCategoriesIfNeed() {
        if (needLoadTree || self.dataSource.isEmpty) {
            GlobalProvider.instance.directoriesProvider.getCategories(true) { result in
                if (self.dataSource.isEmpty) {
                    self.dataSource = result
                    self.tableView.reloadData()
                }
                self.dataSource = result
            }
        }
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        tableView.isUserInteractionEnabled = true
        getCategoriesIfNeed()
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return dataSource.count + 1 // Добавляем хэдэр
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if (indexPath.row != 0) {
            let cell = tableView.dequeueReusableCell(withIdentifier: "CatalogRow", for: indexPath as IndexPath) as! CatalogBasicRow
            cell.catalogNameLabel.text = dataSource[indexPath.row - 1].displayInTreeName
            return cell
        }
        else {
            let cell = tableView.dequeueReusableCell(withIdentifier: "CatalogRowHeader", for: indexPath as IndexPath) as! CatalogBasicRow
            cell.catalogNameLabel.text = parentCategory != nil ? parentCategory?.name : "Все категории"
            return cell
        }
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if(indexPath.row == 0) {
            return
        }
        tableView.isUserInteractionEnabled = false
        let parent = dataSource[indexPath.row - 1]
        let children = parent.children
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        if(!children.isEmpty) {
            let catalogViewController = storyboard.instantiateViewController(withIdentifier: "CatalogViewController") as! CatalogViewController
            catalogViewController.dataSource = children
            catalogViewController.needLoadTree = false
            catalogViewController.parentCategory = parent
            navigationController?.pushViewController(catalogViewController, animated: true)
        }
        else {
            let c = storyboard.instantiateViewController(withIdentifier: "CatalogProductListController") as! CatalogProductListController
            c.catalogName = parent.name
            c.categoryId = parent.id
            navigationController?.pushViewController(c, animated: true)
        }
    }

    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 44.0
    }
}
