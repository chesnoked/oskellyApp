//
//  Cart.swift
//
//  Created by Виталий Хлудеев on 29.10.17
//  Copyright (c) . All rights reserved.
//

import Foundation
import SwiftyJSON

public final class Cart: NSCoding {

  // MARK: Declaration for string constants to be used to decode and also serialize.
  private struct SerializationKeys {
    static let totalPrice = "totalPrice"
    static let effectivePrice = "effectivePrice"
    static let effectiveItems = "effectiveItems"
    static let items = "items"
    static let effectivePriceWithDeliveryCost = "effectivePriceWithDeliveryCost"
    static let noneffectiveItems = "noneffectiveItems"
    static let deliveryCost = "deliveryCost"
  }

  // MARK: Properties
  public var totalPrice: String?
  public var effectivePrice: String?
  public var effectiveItems: [CartItem]!
  public var items: [CartItem]?
  public var effectivePriceWithDeliveryCost: String?
  public var noneffectiveItems: [CartItem]?
  public var deliveryCost: String?

  // MARK: SwiftyJSON Initializers
  /// Initiates the instance based on the object.
  ///
  /// - parameter object: The object of either Dictionary or Array kind that was passed.
  /// - returns: An initialized instance of the class.
  public convenience init(object: Any) {
    self.init(json: JSON(object))
  }

  init() {
    effectiveItems = []
  }

  /// Initiates the instance based on the JSON that was passed.
  ///
  /// - parameter json: JSON object from SwiftyJSON.
  public required init(json: JSON) {
    totalPrice = json[SerializationKeys.totalPrice].string
    effectivePrice = json[SerializationKeys.effectivePrice].string
    if let items = json[SerializationKeys.effectiveItems].array { effectiveItems = items.map { CartItem(json: $0) } }
    if let i = json[SerializationKeys.items].array { items = i.map { CartItem(json: $0) } }
    effectivePriceWithDeliveryCost = json[SerializationKeys.effectivePriceWithDeliveryCost].string
    if let items = json[SerializationKeys.noneffectiveItems].array { noneffectiveItems = items.map { CartItem(json: $0) } }
    deliveryCost = json[SerializationKeys.deliveryCost].string
  }

  /// Generates description of the object in the form of a NSDictionary.
  ///
  /// - returns: A Key value pair containing all valid values in the object.
  public func dictionaryRepresentation() -> [String: Any] {
    var dictionary: [String: Any] = [:]
    if let value = totalPrice { dictionary[SerializationKeys.totalPrice] = value }
    if let value = effectivePrice { dictionary[SerializationKeys.effectivePrice] = value }
    dictionary[SerializationKeys.effectiveItems] = effectiveItems.map { $0.dictionaryRepresentation() }
    if let value = items { dictionary[SerializationKeys.items] = value.map { $0.dictionaryRepresentation() } }
    if let value = effectivePriceWithDeliveryCost { dictionary[SerializationKeys.effectivePriceWithDeliveryCost] = value }
    if let value = noneffectiveItems { dictionary[SerializationKeys.noneffectiveItems] = value.map { $0.dictionaryRepresentation() } }
    if let value = deliveryCost { dictionary[SerializationKeys.deliveryCost] = value }
    return dictionary
  }

  // MARK: NSCoding Protocol
  required public init(coder aDecoder: NSCoder) {
    self.totalPrice = aDecoder.decodeObject(forKey: SerializationKeys.totalPrice) as? String
    self.effectivePrice = aDecoder.decodeObject(forKey: SerializationKeys.effectivePrice) as? String
    self.effectiveItems = aDecoder.decodeObject(forKey: SerializationKeys.effectiveItems) as! [CartItem]
    self.items = aDecoder.decodeObject(forKey: SerializationKeys.items) as? [CartItem]
    self.effectivePriceWithDeliveryCost = aDecoder.decodeObject(forKey: SerializationKeys.effectivePriceWithDeliveryCost) as? String
    self.noneffectiveItems = aDecoder.decodeObject(forKey: SerializationKeys.noneffectiveItems) as? [CartItem]
    self.deliveryCost = aDecoder.decodeObject(forKey: SerializationKeys.deliveryCost) as? String
  }

  public func encode(with aCoder: NSCoder) {
    aCoder.encode(totalPrice, forKey: SerializationKeys.totalPrice)
    aCoder.encode(effectivePrice, forKey: SerializationKeys.effectivePrice)
    aCoder.encode(effectiveItems, forKey: SerializationKeys.effectiveItems)
    aCoder.encode(items, forKey: SerializationKeys.items)
    aCoder.encode(effectivePriceWithDeliveryCost, forKey: SerializationKeys.effectivePriceWithDeliveryCost)
    aCoder.encode(noneffectiveItems, forKey: SerializationKeys.noneffectiveItems)
    aCoder.encode(deliveryCost, forKey: SerializationKeys.deliveryCost)
  }

}
