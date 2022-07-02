//
// Created by Виталий Хлудеев on 16.10.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class SubmitCategoryController : UIViewController, UITableViewDataSource, UITableViewDelegate {

    let tableView = UITableView()

    var parentCategory: ProductCategory? = nil

    var dataSource: [ProductCategory] = []

    var needLoadTree = true

    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.tableFooterView = UIView()
        tableView.dataSource = self
        tableView.delegate = self
        tableView.register(SubmitCategoryRow.self, forCellReuseIdentifier: "CatalogRow")
        tableView.register(SubmitCategoryHeader.self, forCellReuseIdentifier: "CatalogRowHeader")
        tableView.separatorStyle = .none
        tableView.backgroundColor = AppColors.transparent()
        self.view.addSubview(tableView)
        self.navigationItem.title = "Предназначение вещи"
        tableView.snp.makeConstraints { (make) -> Void in
            make.edges.equalTo(view.snp.edges)
        }
        if(needLoadTree) {
            GlobalProvider.instance.directoriesProvider.getCategories(false) { result in
                self.dataSource = result
                self.tableView.reloadData()
            }
        }
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return dataSource.count + 1 // Добавляем хэдэр
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if (indexPath.row != 0) {
            let cell = tableView.dequeueReusableCell(withIdentifier: "CatalogRow", for: indexPath as IndexPath) as! SubmitCategoryRow
            cell.catalogNameLabel.text = dataSource[indexPath.row - 1].name
            return cell
        } else {
            let cell = tableView.dequeueReusableCell(withIdentifier: "CatalogRowHeader", for: indexPath as IndexPath) as! SubmitCategoryHeader
            cell.catalogNameLabel.text = parentCategory != nil ? parentCategory?.name : "К какой категории относится\nпродаваемая вами вещь?"
            return cell
        }
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: false)
        if (indexPath.row == 0) {
            return
        }
        let parent = dataSource[indexPath.row - 1]
        let children = parent.children
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        if (!children.isEmpty) {
            let c = storyboard.instantiateViewController(withIdentifier: "SubmitCategoryController") as! SubmitCategoryController
            c.dataSource = children
            c.parentCategory = parent
            c.needLoadTree = false
            navigationController?.pushViewController(c, animated: true)
        }
        else {
            let c = storyboard.instantiateViewController(withIdentifier: "PublicationSubmitBrandController") as! PublicationSubmitBrandController
            c.selectedCategoryId = parent.id
            navigationController?.pushViewController(c, animated: true)
        }
    }

    public func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        if(indexPath.row == 0) {
            return 132.0
        }
        return 66.0
    }
}