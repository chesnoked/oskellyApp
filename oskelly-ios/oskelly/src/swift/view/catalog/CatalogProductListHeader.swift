//
// Created by Виталий Хлудеев on 11.12.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class CatalogProductListHeader : UICollectionViewCell {

    let filters = CatalogHeaderButton()
    let sort = CatalogHeaderButton()
    let verticalSeparator = UIView()
    var controller: CatalogProductListController!

    override init(frame: CGRect) {
        super.init(frame: frame)
        contentView.backgroundColor = .white
        addFiltersButton()
        addVerticalSeparator()
        addSortButton()
        addBottomSeparator()
    }

    func render(controller: CatalogProductListController) {
        self.controller = controller
    }

    private func addSortButton() {
        contentView.addSubview(sort)
        sort.snp.makeConstraints({m in
            m.right.equalTo(verticalSeparator.snp.left)
            m.top.equalTo(contentView)
            m.bottom.equalTo(contentView)
            m.width.equalTo(90)
        })
        sort.setTitle("Сортировка", for: .normal)
        sort.addTarget(self, action: #selector(self.sortClick), for: .touchUpInside)
        filters.addTarget(self, action: #selector(self.filtersClick), for: .touchUpInside)
    }

    func sortClick() {
        let c = controller.storyboard?.instantiateViewController(withIdentifier: "CatalogSortController") as! CatalogSortController
        c.previousController = controller
        controller.navigationController?.pushViewController(c, animated: true)
    }

    func filtersClick() {
        let c = controller.storyboard?.instantiateViewController(withIdentifier: "CatalogFilterGroupsController") as! CatalogFilterGroupsController
        c.previousController = controller
        controller.navigationController?.pushViewController(c, animated: true)
    }

    private func addBottomSeparator() {
        let separator = UIView()
        separator.backgroundColor = AppColors.separator()
        contentView.addSubview(separator)
        separator.snp.makeConstraints { (make) -> Void in
            make.height.equalTo(0.5)
            make.bottom.equalTo(contentView)
            make.right.equalTo(contentView)
            make.left.equalTo(contentView)
        }
    }

    private func addVerticalSeparator() {
        verticalSeparator.backgroundColor = AppColors.separator()
        contentView.addSubview(verticalSeparator)
        verticalSeparator.snp.makeConstraints { (make) -> Void in
            make.width.equalTo(1.5)
            make.height.equalTo(47.0 * 0.6)
            make.centerY.equalTo(contentView)
            make.right.equalTo(filters.snp.left)
        }
    }

    private func addFiltersButton() {
        contentView.addSubview(filters)
        filters.snp.makeConstraints({m in
            m.right.equalTo(contentView)
            m.top.equalTo(contentView)
            m.bottom.equalTo(contentView)
            m.width.equalTo(70)
        })
        filters.setTitle("Фильтры", for: .normal)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}