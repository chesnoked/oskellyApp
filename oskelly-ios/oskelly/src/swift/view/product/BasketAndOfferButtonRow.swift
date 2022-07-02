//
// Created by Виталий Хлудеев on 24.05.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class BasketAndOfferButtonRow : UITableViewCell {

    let productCartAddButton = DarkButton()
    let deleteButton = DarkButton()
    let makeMeOfferButton = WhiteButton()

    var product: Product!
    var controller: ProductViewController!

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        addProductCartAddButton(container: contentView)
        addDeleteButton(container: contentView)
        addMakeMeOfferButton(container: contentView)
    }

    func addDeleteButton(container: UIView) {
        deleteButton.setTitle("Снять с продажи", for: .normal)
        container.addSubview(deleteButton)
        deleteButton.snp.makeConstraints { (make) -> Void in
            make.left.equalTo(container).inset(15)
            make.right.equalTo(container).inset(15)
            make.top.equalTo(container).inset(18)
        }
        deleteButton.addTarget(self, action: #selector(self.deleteButtonTapped(_:)), for: .touchUpInside)
    }

    func addProductCartAddButton(container: UIView) {
        productCartAddButton.setTitle("Добавить в корзину", for: .normal)
        container.addSubview(productCartAddButton)
        productCartAddButton.snp.makeConstraints { (make) -> Void in
            make.left.equalTo(container).inset(15)
            make.right.equalTo(container).inset(15)
            make.top.equalTo(container).inset(18)
        }
        productCartAddButton.addTarget(self, action: #selector(self.cartButtonTapped(_:)), for: .touchUpInside)
    }

    func addMakeMeOfferButton(container: UIView) {
        makeMeOfferButton.setTitle("Предложить свою цену", for: .normal)
        container.addSubview(makeMeOfferButton)
        makeMeOfferButton.snp.makeConstraints { (make) -> Void in
            make.width.equalTo(productCartAddButton.snp.width)
            make.centerX.equalTo(productCartAddButton.snp.centerX)
            make.top.equalTo(productCartAddButton.snp.bottom).offset(10)
            make.bottom.equalTo(container)
        }
        makeMeOfferButton.addTarget(self, action: #selector(self.offerButtonTapped(_:)), for: .touchUpInside)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    func render(product: Product, controller: ProductViewController) {
        self.product = product
        self.controller = controller

        GlobalProvider.instance.accountProvider.getCurrent(completionHandler: {a in
            self.renderElements(product: product, account: a)
        }, needSynchronize: false)
    }

    private func renderElements(product: Product, account: Account) {
        makeMeOfferButton.setTitle(product.offerRelated?.negotiationControlText ?? "Предложить свою цену", for: .normal)
        let hideMakeMeOfferButton = (product.offerRelated?.allowsNegotiation == nil || account.id == product.sellerId)
        makeMeOfferButton.isHidden = hideMakeMeOfferButton
        makeMeOfferButton.snp.remakeConstraints { m in
            m.width.equalTo(productCartAddButton.snp.width)
            m.centerX.equalTo(productCartAddButton.snp.centerX)
            m.top.equalTo(productCartAddButton.snp.bottom).offset(hideMakeMeOfferButton ? 0 : 10)
            m.height.equalTo(hideMakeMeOfferButton ? 0 : 50)
            m.bottom.equalTo(contentView)
        }
        if(product.canBeAddedToCart ?? true) {
            productCartAddButton.enable()
            productCartAddButton.setTitle("Добавить в корзину", for: .normal)
        }
        else {
            productCartAddButton.disable()
            productCartAddButton.setTitle(product.reasonWhyItCannotBeAddedToCart ?? "Нельзя добавить в корзину", for: .normal)
        }
        if(product.canBeAddedToCart ?? true) {
            deleteButton.enable()
            deleteButton.setTitle("Снять с продажи", for: .normal)
        }
        else {
            deleteButton.disable()
            deleteButton.setTitle(product.reasonWhyItCannotBeAddedToCart ?? "Нельзя снять с продажи", for: .normal)
        }
        productCartAddButton.isHidden = (account.id == product.sellerId)
        deleteButton.isHidden = (account.id != product.sellerId)
    }


    func offerButtonTapped(_ sender: Any) {
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        let c = storyboard.instantiateViewController(withIdentifier: "ProductOfferController") as! ProductOfferController
        c.product = product
        c.parentController = controller
        controller.navigationController?.pushViewController(c, animated: true)
    }

    func cartButtonTapped(_ sender: Any) {
        if(product.size!.values!.count > 1) {
            let actionSheet = UIAlertController(title: "Выберите размер", message: "Размер: " + product.size!.type!, preferredStyle: UIAlertControllerStyle.actionSheet)
            let optionMenu = UIAlertController(title: nil, message: nil, preferredStyle: .actionSheet)
            optionMenu.popoverPresentationController?.sourceView = controller.view
            product.size?.values?.forEach({
                let sizeValue = $0
                let action = UIAlertAction(title: sizeValue.value, style: UIAlertActionStyle.default) { (action) in
                    self.addToCart(sizeValue: sizeValue)
                }
                actionSheet.addAction(action)
            })


            let cancelAction = UIAlertAction(title: "Отмена", style: UIAlertActionStyle.cancel) { (action) in
            }
            actionSheet.addAction(cancelAction)

            controller.present(actionSheet, animated: true, completion: nil)
        }
        else {
            addToCart(sizeValue: product.size!.values!.first!)
        }

    }

    func deleteButtonTapped(_ sender: Any) {
        if let id = product.id {
            GlobalProvider.instance.getApiRequester().deleteProduct(productId: id, completionHandler: {
                self.deleteButton.disable()
                self.deleteButton.setTitle("Нет в продаже", for: .normal)
            })
        }
    }

    func addToCart(sizeValue: ProductSizeValue) {
        GlobalProvider
                .instance
                .getApiRequester()
                .addItemToCart(productId: self.product.id, sizeId: sizeValue.id!, price: sizeValue.lowestPrice!, completionHandler: {
                    self.productCartAddButton.setTitle("В корзине", for: .normal)
                    self.productCartAddButton.disable()
                    GlobalProvider.instance.cartProvider.synchronizeCart()
                    GlobalProvider.instance.cartIconService.refreshCount()
                })
    }
}
