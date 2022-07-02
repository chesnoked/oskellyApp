//
//  OrderPosition.swift
//
//  Created by Виталий Хлудеев on 31.10.17
//  Copyright (c) . All rights reserved.
//

import Foundation
import SwiftyJSON

public final class OrderPosition: NSCoding {

  // MARK: Declaration for string constants to be used to decode and also serialize.
  private struct SerializationKeys {
    static let productId = "productId"
    static let productName = "productName"
    static let productItemId = "productItemId"
    static let sellerNick = "sellerNick"
    static let productPrice = "productPrice"
    static let deliveryCost = "deliveryCost"
    static let imageUrl = "imageUrl"
    static let brandName = "brandName"
    static let sellerId = "sellerId"
    static let productSize = "productSize"
  }

  // MARK: Properties
  public var productId: Int!
  public var productName: String?
  public var productItemId: Int!
  public var sellerNick: String?
  public var productPrice: Float!
  public var deliveryCost: Float?
  public var imageUrl: String?
  public var brandName: String?
  public var sellerId: Int?
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
    productId = json[SerializationKeys.productId].int
    productName = json[SerializationKeys.productName].string
    productItemId = json[SerializationKeys.productItemId].int
    sellerNick = json[SerializationKeys.sellerNick].string
    productPrice = json[SerializationKeys.productPrice].float
    deliveryCost = json[SerializationKeys.deliveryCost].float
    imageUrl = json[SerializationKeys.imageUrl].string
    brandName = json[SerializationKeys.brandName].string
    sellerId = json[SerializationKeys.sellerId].int
    productSize = json[SerializationKeys.productSize].string
  }

  /// Generates description of the object in the form of a NSDictionary.
  ///
  /// - returns: A Key value pair containing all valid values in the object.
  public func dictionaryRepresentation() -> [String: Any] {
    var dictionary: [String: Any] = [:]
    if let value = productId { dictionary[SerializationKeys.productId] = value }
    if let value = productName { dictionary[SerializationKeys.productName] = value }
    if let value = productItemId { dictionary[SerializationKeys.productItemId] = value }
    if let value = sellerNick { dictionary[SerializationKeys.sellerNick] = value }
    if let value = productPrice { dictionary[SerializationKeys.productPrice] = value }
    if let value = deliveryCost { dictionary[SerializationKeys.deliveryCost] = value }
    if let value = imageUrl { dictionary[SerializationKeys.imageUrl] = value }
    if let value = brandName { dictionary[SerializationKeys.brandName] = value }
    if let value = sellerId { dictionary[SerializationKeys.sellerId] = value }
    if let value = productSize { dictionary[SerializationKeys.productSize] = value }
    return dictionary
  }

  // MARK: NSCoding Protocol
  required public init(coder aDecoder: NSCoder) {
    self.productId = aDecoder.decodeObject(forKey: SerializationKeys.productId) as? Int
    self.productName = aDecoder.decodeObject(forKey: SerializationKeys.productName) as? String
    self.productItemId = aDecoder.decodeObject(forKey: SerializationKeys.productItemId) as? Int
    self.sellerNick = aDecoder.decodeObject(forKey: SerializationKeys.sellerNick) as? String
    self.productPrice = aDecoder.decodeObject(forKey: SerializationKeys.productPrice) as? Float
    self.deliveryCost = aDecoder.decodeObject(forKey: SerializationKeys.deliveryCost) as? Float
    self.imageUrl = aDecoder.decodeObject(forKey: SerializationKeys.imageUrl) as? String
    self.brandName = aDecoder.decodeObject(forKey: SerializationKeys.brandName) as? String
    self.sellerId = aDecoder.decodeObject(forKey: SerializationKeys.sellerId) as? Int
    self.productSize = aDecoder.decodeObject(forKey: SerializationKeys.productSize) as? String
  }

  public func encode(with aCoder: NSCoder) {
    aCoder.encode(productId, forKey: SerializationKeys.productId)
    aCoder.encode(productName, forKey: SerializationKeys.productName)
    aCoder.encode(productItemId, forKey: SerializationKeys.productItemId)
    aCoder.encode(sellerNick, forKey: SerializationKeys.sellerNick)
    aCoder.encode(productPrice, forKey: SerializationKeys.productPrice)
    aCoder.encode(deliveryCost, forKey: SerializationKeys.deliveryCost)
    aCoder.encode(imageUrl, forKey: SerializationKeys.imageUrl)
    aCoder.encode(brandName, forKey: SerializationKeys.brandName)
    aCoder.encode(sellerId, forKey: SerializationKeys.sellerId)
    aCoder.encode(productSize, forKey: SerializationKeys.productSize)
  }

}
