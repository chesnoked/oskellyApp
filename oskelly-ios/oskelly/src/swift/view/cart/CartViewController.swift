//
//  CartViewController.swift
//  oskelly
//
//  Created by Виталий Хлудеев on 05.06.17.
//  Copyright © 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class CartViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {
    
    let tableView = UITableView()
    let footer = CartFooter()

    var cart: Cart!
    
    var cartBackup : Cart!

    var itemsToDelete: [CartItem] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        view.addSubview(tableView)
        view.addSubview(footer)
        tableView.register(CartRow.self, forCellReuseIdentifier: "CartRow")
        tableView.dataSource = self
        tableView.delegate = self
        tableView.separatorStyle = .none
        footer.controller = self
        tableView.snp.makeConstraints { make -> Void in
            make.edges.equalTo(view).inset(UIEdgeInsetsMake(0, 0, OrderBaseFooter.height, 0))
        }
        footer.snp.makeConstraints { make -> Void in
            make.top.equalTo(tableView.snp.bottom)
            make.left.equalTo(view)
            make.right.equalTo(view)
            make.bottom.equalTo(view)
        }
        
        navigationItem.title = "Корзина"
        navigationItem.rightBarButtonItem = UIBarButtonItem(title: "Править", style: UIBarButtonItemStyle.plain, target: self, action: #selector(CartViewController.editButtonPressed))
        navigationItem.leftBarButtonItem = UIBarButtonItem(title: "Отмена", style: UIBarButtonItemStyle.plain, target: self, action: #selector(CartViewController.cancelButtonPressed))
        navigationItem.hidesBackButton = true
        cart = Cart()
        GlobalProvider.instance.cartProvider.getCart(completionHandler: {cart in
            self.cart = cart
            self.footer.render(cart: cart)
            self.tableView.reloadData()
        })
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        GlobalProvider.instance.cartProvider.getCart(completionHandler: {cart in
            self.cart = cart
            self.tableView.reloadData()
        })
    }
    
    public func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "CartRow") as! CartRow
        cell.render(cartItem: cart.effectiveItems[indexPath.row])
        return cell
    }
    
    public func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return cart.effectiveItems.count
    }
    
    public func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 105
    }
    
    public func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool
    {
        return true
    }
    
    public func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCellEditingStyle, forRowAt indexPath: IndexPath)
    {
        if editingStyle == .delete
        {
            self.tableView.beginUpdates()
            itemsToDelete.append(self.cart.effectiveItems[indexPath.row])
            self.cart.effectiveItems.remove(at: indexPath.row)
            self.tableView.deleteRows(at: [indexPath], with: .left)
            self.tableView.endUpdates()
            footer.render(cart: cart)
        }
    }

    func tableView(_ tableView: UITableView, editingStyleForRowAt indexPath: IndexPath) -> UITableViewCellEditingStyle {
        if (tableView.isEditing) {
            return .delete
        }
        return .none
    }
    
    public func tableView(_ tableView: UITableView, titleForDeleteConfirmationButtonForRowAt indexPath: IndexPath) -> String? {
        return "Удалить"
    }
    
    
    func editButtonPressed() {
        tableView.setEditing(!tableView.isEditing, animated: true)
        if tableView.isEditing == true {
            cartBackup = Cart(object: cart.dictionaryRepresentation()) // делам бэкап
            navigationItem.rightBarButtonItem = UIBarButtonItem(title: "Ок", style: UIBarButtonItemStyle.plain, target: self, action: #selector(CartViewController.editButtonPressed))
        }
        else {
            // отправка на сервер удаленных данных
            itemsToDelete.forEach({item in
                GlobalProvider.instance.getApiRequester().deleteCartItem(cartItemId: item.itemId!, completionHandler: {
                    GlobalProvider.instance.cartIconService.refreshCount()
                })
            })
            navigationItem.rightBarButtonItem = UIBarButtonItem(title: "Править", style: UIBarButtonItemStyle.plain, target: self, action: #selector(CartViewController.editButtonPressed))
        }
    }
    
    func cancelButtonPressed() {
        if tableView.isEditing == true {
            cart = Cart(object: cartBackup.dictionaryRepresentation())
            itemsToDelete.removeAll()
            tableView.reloadData()
            footer.render(cart: cart)
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.005, execute: {
                self.tableView.setEditing(false, animated: true)
            })
            navigationItem.rightBarButtonItem = UIBarButtonItem(title: "Править", style: UIBarButtonItemStyle.plain, target: self, action: #selector(CartViewController.editButtonPressed))
        }
        else {
            self.navigationController?.popViewController(animated: true)
        }
    }
}
