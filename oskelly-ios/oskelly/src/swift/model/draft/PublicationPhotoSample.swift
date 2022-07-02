//
//  PublicationPhotoSample.swift
//
//  Created by Виталий Хлудеев on 31.10.17
//  Copyright (c) . All rights reserved.
//

import Foundation
import SwiftyJSON

public final class PublicationPhotoSample: NSCoding {

  // MARK: Declaration for string constants to be used to decode and also serialize.
  private struct SerializationKeys {
    static let imagePath = "imagePath"
    static let photoOrder = "photoOrder"
  }

  // MARK: Properties
  public var imagePath: String?
  public var photoOrder: Int?

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
    imagePath = json[SerializationKeys.imagePath].string
    photoOrder = json[SerializationKeys.photoOrder].int
  }

  /// Generates description of the object in the form of a NSDictionary.
  ///
  /// - returns: A Key value pair containing all valid values in the object.
  public func dictionaryRepresentation() -> [String: Any] {
    var dictionary: [String: Any] = [:]
    if let value = imagePath { dictionary[SerializationKeys.imagePath] = value }
    if let value = photoOrder { dictionary[SerializationKeys.photoOrder] = value }
    return dictionary
  }

  // MARK: NSCoding Protocol
  required public init(coder aDecoder: NSCoder) {
    self.imagePath = aDecoder.decodeObject(forKey: SerializationKeys.imagePath) as? String
    self.photoOrder = aDecoder.decodeObject(forKey: SerializationKeys.photoOrder) as? Int
  }

  public func encode(with aCoder: NSCoder) {
    aCoder.encode(imagePath, forKey: SerializationKeys.imagePath)
    aCoder.encode(photoOrder, forKey: SerializationKeys.photoOrder)
  }

}
