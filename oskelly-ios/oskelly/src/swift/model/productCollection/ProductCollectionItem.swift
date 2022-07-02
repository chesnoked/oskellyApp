//
//  ProductCollectionItem.swift
//
//  Created by Виталий Хлудеев on 13.11.17
//  Copyright (c) . All rights reserved.
//

import Foundation
import SwiftyJSON

public final class ProductCollectionItem: NSCoding {

  // MARK: Declaration for string constants to be used to decode and also serialize.
  private struct SerializationKeys {
    static let brand = "brand"
    static let id = "id"
    static let image = "image"
    static let notUsedYet = "notUsedYet"
    static let size = "size"
    static let likesCount = "likesCount"
    static let price = "price"
    static let category = "category"
    static let doILike = "doILike"
    static let hasDiscount = "hasDiscount"
  }

  // MARK: Properties
  public var brand: String?
  public var id: Int?
  public var image: String?
  public var notUsedYet: Bool? = false
  public var size: ProductSize?
  public var likesCount: Int?
  public var price: String?
  public var category: String?
  public var doILike: Bool? = false
  public var hasDiscount: Bool? = false

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
    brand = json[SerializationKeys.brand].string
    id = json[SerializationKeys.id].int
    image = json[SerializationKeys.image].string
    notUsedYet = json[SerializationKeys.notUsedYet].boolValue
    size = ProductSize(json: json[SerializationKeys.size])
    likesCount = json[SerializationKeys.likesCount].int
    price = json[SerializationKeys.price].string
    category = json[SerializationKeys.category].string
    doILike = json[SerializationKeys.doILike].boolValue
    hasDiscount = json[SerializationKeys.hasDiscount].boolValue
  }

  /// Generates description of the object in the form of a NSDictionary.
  ///
  /// - returns: A Key value pair containing all valid values in the object.
  public func dictionaryRepresentation() -> [String: Any] {
    var dictionary: [String: Any] = [:]
    if let value = brand { dictionary[SerializationKeys.brand] = value }
    if let value = id { dictionary[SerializationKeys.id] = value }
    if let value = image { dictionary[SerializationKeys.image] = value }
    dictionary[SerializationKeys.notUsedYet] = notUsedYet
    if let value = size { dictionary[SerializationKeys.size] = value.dictionaryRepresentation() }
    if let value = likesCount { dictionary[SerializationKeys.likesCount] = value }
    if let value = price { dictionary[SerializationKeys.price] = value }
    if let value = category { dictionary[SerializationKeys.category] = value }
    dictionary[SerializationKeys.doILike] = doILike
    dictionary[SerializationKeys.hasDiscount] = hasDiscount
    return dictionary
  }

  // MARK: NSCoding Protocol
  required public init(coder aDecoder: NSCoder) {
    self.brand = aDecoder.decodeObject(forKey: SerializationKeys.brand) as? String
    self.id = aDecoder.decodeObject(forKey: SerializationKeys.id) as? Int
    self.image = aDecoder.decodeObject(forKey: SerializationKeys.image) as? String
    self.notUsedYet = aDecoder.decodeBool(forKey: SerializationKeys.notUsedYet)
    self.size = aDecoder.decodeObject(forKey: SerializationKeys.size) as? ProductSize
    self.likesCount = aDecoder.decodeObject(forKey: SerializationKeys.likesCount) as? Int
    self.price = aDecoder.decodeObject(forKey: SerializationKeys.price) as? String
    self.category = aDecoder.decodeObject(forKey: SerializationKeys.category) as? String
    self.doILike = aDecoder.decodeBool(forKey: SerializationKeys.doILike)
    self.hasDiscount = aDecoder.decodeBool(forKey: SerializationKeys.hasDiscount)
  }

  public func encode(with aCoder: NSCoder) {
    aCoder.encode(brand, forKey: SerializationKeys.brand)
    aCoder.encode(id, forKey: SerializationKeys.id)
    aCoder.encode(image, forKey: SerializationKeys.image)
    aCoder.encode(notUsedYet, forKey: SerializationKeys.notUsedYet)
    aCoder.encode(size, forKey: SerializationKeys.size)
    aCoder.encode(likesCount, forKey: SerializationKeys.likesCount)
    aCoder.encode(price, forKey: SerializationKeys.price)
    aCoder.encode(category, forKey: SerializationKeys.category)
    aCoder.encode(doILike, forKey: SerializationKeys.doILike)
    aCoder.encode(hasDiscount, forKey: SerializationKeys.hasDiscount)
  }
}