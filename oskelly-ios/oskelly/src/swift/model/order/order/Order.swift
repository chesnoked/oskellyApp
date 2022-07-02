//
//  Order.swift
//
//  Created by Виталий Хлудеев on 31.10.17
//  Copyright (c) . All rights reserved.
//

import Foundation
import SwiftyJSON

public final class Order: NSCoding {

  // MARK: Declaration for string constants to be used to decode and also serialize.
  private struct SerializationKeys {
    static let state = "state"
    static let id = "id"
    static let readyForPayment = "readyForPayment"
    static let deliveryRequisite = "deliveryRequisite"
    static let appliedDiscount = "appliedDiscount"
    static let stateName = "stateName"
    static let createTime = "createTime"
    static let payable = "payable"
    static let items = "items"
    static let paidAtThisMoment = "paidAtThisMoment"
    static let price = "price"
    static let deliveryCost = "deliveryCost"
  }

  // MARK: Properties
  public var state: String?
  public var id: Int!
  public var readyForPayment: Bool? = false
  public var deliveryRequisite: DeliveryRequisite?
  public var appliedDiscount: Discount?
  public var stateName: String?
  public var createTime: String?
  public var payable: Bool? = false
  public var items: [OrderPosition]! = []
  public var paidAtThisMoment: Bool? = false
  public var price: Float!
  public var deliveryCost: Float!

  // MARK: SwiftyJSON Initializers
  /// Initiates the instance based on the object.
  ///
  /// - parameter object: The object of either Dictionary or Array kind that was passed.
  /// - returns: An initialized instance of the class.
  public convenience init(object: Any) {
    self.init(json: JSON(object))
  }

  /// Initiates the instance based on the JSON that was passed.
  ///
  /// - parameter json: JSON object from SwiftyJSON.
  public required init(json: JSON) {
    state = json[SerializationKeys.state].string
    id = json[SerializationKeys.id].int
    readyForPayment = json[SerializationKeys.readyForPayment].boolValue
    deliveryRequisite = DeliveryRequisite(json: json[SerializationKeys.deliveryRequisite])
    appliedDiscount = Discount(json: json[SerializationKeys.appliedDiscount])
    stateName = json[SerializationKeys.stateName].string
    createTime = json[SerializationKeys.createTime].string
    payable = json[SerializationKeys.payable].boolValue
    if let i = json[SerializationKeys.items].array { items = i.map { OrderPosition(json: $0) } }
    paidAtThisMoment = json[SerializationKeys.paidAtThisMoment].boolValue
    price = json[SerializationKeys.price].float
    deliveryCost = json[SerializationKeys.deliveryCost].float
  }

  /// Generates description of the object in the form of a NSDictionary.
  ///
  /// - returns: A Key value pair containing all valid values in the object.
  public func dictionaryRepresentation() -> [String: Any] {
    var dictionary: [String: Any] = [:]
    if let value = state { dictionary[SerializationKeys.state] = value }
    if let value = id { dictionary[SerializationKeys.id] = value }
    dictionary[SerializationKeys.readyForPayment] = readyForPayment
    if let value = deliveryRequisite { dictionary[SerializationKeys.deliveryRequisite] = value.dictionaryRepresentation() }
    if let value = appliedDiscount { dictionary[SerializationKeys.appliedDiscount] = value.dictionaryRepresentation() }
    if let value = stateName { dictionary[SerializationKeys.stateName] = value }
    if let value = createTime { dictionary[SerializationKeys.createTime] = value }
    dictionary[SerializationKeys.payable] = payable
    if let value = items { dictionary[SerializationKeys.items] = value.map { $0.dictionaryRepresentation() } }
    dictionary[SerializationKeys.paidAtThisMoment] = paidAtThisMoment
    if let value = price { dictionary[SerializationKeys.price] = value }
    if let value = deliveryCost { dictionary[SerializationKeys.deliveryCost] = value }
    return dictionary
  }

  // MARK: NSCoding Protocol
  required public init(coder aDecoder: NSCoder) {
    self.state = aDecoder.decodeObject(forKey: SerializationKeys.state) as? String
    self.id = aDecoder.decodeObject(forKey: SerializationKeys.id) as? Int
    self.readyForPayment = aDecoder.decodeBool(forKey: SerializationKeys.readyForPayment)
    self.deliveryRequisite = aDecoder.decodeObject(forKey: SerializationKeys.deliveryRequisite) as? DeliveryRequisite
    self.appliedDiscount = aDecoder.decodeObject(forKey: SerializationKeys.appliedDiscount) as? Discount
    self.stateName = aDecoder.decodeObject(forKey: SerializationKeys.stateName) as? String
    self.createTime = aDecoder.decodeObject(forKey: SerializationKeys.createTime) as? String
    self.payable = aDecoder.decodeBool(forKey: SerializationKeys.payable)
    self.items = aDecoder.decodeObject(forKey: SerializationKeys.items) as? [OrderPosition]
    self.paidAtThisMoment = aDecoder.decodeBool(forKey: SerializationKeys.paidAtThisMoment)
    self.price = aDecoder.decodeObject(forKey: SerializationKeys.price) as? Float
    self.deliveryCost = aDecoder.decodeObject(forKey: SerializationKeys.deliveryCost) as? Float
  }

  public func encode(with aCoder: NSCoder) {
    aCoder.encode(state, forKey: SerializationKeys.state)
    aCoder.encode(id, forKey: SerializationKeys.id)
    aCoder.encode(readyForPayment, forKey: SerializationKeys.readyForPayment)
    aCoder.encode(deliveryRequisite, forKey: SerializationKeys.deliveryRequisite)
    aCoder.encode(appliedDiscount, forKey: SerializationKeys.appliedDiscount)
    aCoder.encode(stateName, forKey: SerializationKeys.stateName)
    aCoder.encode(createTime, forKey: SerializationKeys.createTime)
    aCoder.encode(payable, forKey: SerializationKeys.payable)
    aCoder.encode(items, forKey: SerializationKeys.items)
    aCoder.encode(paidAtThisMoment, forKey: SerializationKeys.paidAtThisMoment)
    aCoder.encode(price, forKey: SerializationKeys.price)
    aCoder.encode(deliveryCost, forKey: SerializationKeys.deliveryCost)
  }

}
