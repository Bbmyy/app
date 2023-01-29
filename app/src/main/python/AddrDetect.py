import cpca
def main(str="分店位于徐汇区虹漕路461号58号楼5楼和泉州市洛江区万安塘西工业区以及南京鼓楼区"):
    df = cpca.transform_text_with_addrs(str)
    return df