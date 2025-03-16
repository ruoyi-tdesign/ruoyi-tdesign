import type { DefinitionConfigVO } from '@/api/workflow/definitionConfig/types';
import type { NodeConfigVO } from '@/api/workflow/nodeConfig/types';

export interface RouterJumpVo {
  wfNodeConfigVo: NodeConfigVO;
  wfDefinitionConfigVo: DefinitionConfigVO;
  businessKey: string;
  taskId: string | number;
  type: string;
}

export interface StartProcessBo {
  businessKey: string | number;
  tableName: string;
  variables: any;
}
