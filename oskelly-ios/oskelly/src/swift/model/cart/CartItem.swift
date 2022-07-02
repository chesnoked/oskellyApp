//
//  CartItemOld.swift
//
//  Created by Виталий Хлудеев on 29.10.17
//  Copyright (c) . All rights reserved.
//

import Foundation
import SwiftyJSON

public final class CartItem: NSCoding {

  // MARK: Declaration for string constants to be used to decode and also serialize.
  private struct SerializationKeys {
    static let itemId = "itemId"
    static let productId = "productId"
    static let productName = "productName"
    static let productPrice = "productPrice"
    static let isEffective = "isEffective"
    static let imageUrl = "imageUrl"
    static let brandName = "brandName"
    static let deliveryCost = "deliveryCost"
    static let productSize = "productSize"
  }

  // MARK: Properties
  public var itemId: Int!
  public var productId: Int?
  public var productName: String?
  public var productPrice: Float?
  public var isEffective: Bool? = false
  public var imageUrl: String?
  public var brandName: String?
  public var deliveryCost: Float?
  public var productSize: String?

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
    itemId = json[SerializationKeys.itemId].int
    productId = json[SerializationKeys.productId].int
    productName = json[SerializationKeys.productName].string
    productPrice = json[SerializationKeys.productPrice].float
    isEffective = json[SerializationKeys.isEffective].boolValue
    if let value = json[SerializationKeys.imageUrl].string { imageUrl = value }
    brandName = json[SerializationKeys.brandName].string
    deliveryCost = json[SerializationKeys.deliveryCost].float
    productSize = json[SerializationKeys.productSize].string
  }

  /// Generates description of the object in the form of a NSDictionary.
  ///
  /// - returns: A Key value pair containing all valid values in the object.
  public func dictionaryRepresentation() -> [String: Any] {
    var dictionary: [String: Any] = [:]
    if let value = itemId { dictionary[SerializationKeys.itemId] = value }
    if let value = productId { dictionary[SerializationKeys.productId] = value }
    if let value = productName { dictionary[SerializationKeys.productName] = value }
    if let value = productPrice { dictionary[SerializationKeys.productPrice] = value }
    dictionary[SerializationKeys.isEffective] = isEffective
    if let value = imageUrl { dictionary[SerializationKeys.imageUrl] = value }
    if let value = brandName { dictionary[SerializationKeys.brandName] = value }
    if let value = deliveryCost { dictionary[SerializationKeys.deliveryCost] = value }
    if let value = productSize { dictionary[SerializationKeys.productSize] = value }
    return dictionary
  }

  // MARK: NSCoding Protocol
  required public init(coder aDecoder: NSCoder) {
    self.itemId = aDecoder.decodeObject(forKey: SerializationKeys.itemId) as? Int
    self.productId = aDecoder.decodeObject(forKey: SerializationKeys.productId) as? Int
    self.productName = aDecoder.decodeObject(forKey: SerializationKeys.productName) as? String
    self.productPrice = aDecoder.decodeObject(forKey: SerializationKeys.productPrice) as? Float
    self.isEffective = aDecoder.decodeBool(forKey: SerializationKeys.isEffective)
    self.imageUrl = aDecoder.decodeObject(forKey: SerializationKeys.imageUrl) as? String
    self.brandName = aDecoder.decodeObject(forKey: SerializationKeys.brandName) as? String
    self.deliveryCost = aDecoder.decodeObject(forKey: SerializationKeys.deliveryCost) as? Float
    self.productSize = aDecoder.decodeObject(forKey: SerializationKeys.productSize) as? String
  }

  public func encode(with aCoder: NSCoder) {
    aCoder.encode(itemId, forKey: SerializationKeys.itemId)
    aCoder.encode(productId, forKey: SerializationKeys.productId)
    aCoder.encode(productName, forKey: SerializationKeys.productName)
    aCoder.encode(productPrice, forKey: SerializationKeys.productPrice)
    aCoder.encode(isEffective, forKey: SerializationKeys.isEffective)
    aCoder.encode(imageUrl, forKey: SerializationKeys.imageUrl)
    aCoder.encode(brandName, forKey: SerializationKeys.brandName)
    aCoder.encode(deliveryCost, forKey: SerializationKeys.deliveryCost)
    aCoder.encode(productSize, forKey: SerializationKeys.productSize)
  }

}
