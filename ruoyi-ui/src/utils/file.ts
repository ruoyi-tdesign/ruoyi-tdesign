/**
 * Blob对象转File对象
 * @param blob
 * @param fileName
 * @param fileMimeType
 */
export function blobToFile(blob: Blob, fileName: string, fileMimeType: string) {
  // 创建一个新的File对象
  // 在创建File对象时，需要使用Blob的slice方法来克隆Blob
  // 因为File构造函数接受的不是Blob对象，而是一个Blob对象数组（或者是一个由Blob对象构成的Array-Like对象）
  const newBlob = blob.slice(0, blob.size, fileMimeType);
  return new File([newBlob], fileName, {
    type: fileMimeType,
    lastModified: new Date().getTime(),
  });
}
